package com.example.DIVE2025.domain.rescued.service;


import com.example.DIVE2025.domain.rescued.dto.RescuedApiItemDto;
import com.example.DIVE2025.domain.rescued.dto.RescuedApiResponse;
import com.example.DIVE2025.domain.rescued.entity.Rescued;
import com.example.DIVE2025.domain.rescued.enums.ProtectionStatus;
import com.example.DIVE2025.domain.rescued.mapper.RescuedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RescuedImportService {

    private final RescuedMapper rescuedMapper;
    private final RestTemplate rest;

    @Value("${external.abandon.base-url}") private String baseUrl;
    @Value("${external.abandon.path}") private String path;
    @Value("${external.abandon.service-key}") private String serviceKey;
    @Value("${external.abandon.rows:1000}") private int rows;

    private static final DateTimeFormatter API_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** 1) 초기 적재: 3년치 + 보호중만 */
    public int initialBuild3YearsProtectOnly() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusYears(3);
        List<String> regs = rescuedMapper.selectAllCareRegNos();
        int changed = 0;
        for (String reg : regs) {
            changed += fetchAndApply(start, end, reg, /*state=*/"protect");
        }
        log.info("Initial build changed rows={}", changed);
        return changed;
    }

    /** 2) 정기 동기화: 최근 N일(기본 2~3일), state 없이 가져와서 상태 문자열로 판단 */
    public int syncRecentDays(int recentDays) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(recentDays);
        List<String> regs = rescuedMapper.selectAllCareRegNos();
        int changed = 0;
        for (String reg : regs) {
            changed += fetchAndApply(start, end, reg, /*state=*/null);
        }
        log.info("Sync changed rows={}", changed);
        return changed;
    }

    /** 공통 fetch + 적용 (state가 null이면 파라미터 생략) */
    private int fetchAndApply(LocalDate start, LocalDate end, String careRegNo, String stateOrNull) {
        int affected = 0;
        int page = 1;
        for (;;) {
            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("care_reg_no", careRegNo)
                    .queryParam("bgnde", start.format(API_FMT))
                    .queryParam("endde", end.format(API_FMT))
                    .queryParam("pageNo", page)
                    .queryParam("numOfRows", rows)
                    .queryParam("_type", "json")
                    .build(true) // 키가 이미 인코딩된 형태면 true, 아니면 .encode() 권장
                    .toUri();

            // state 지정이 필요한 호출(초기 적재)인 경우만 추가
            if (stateOrNull != null) {
                uri = UriComponentsBuilder.fromUri(uri)
                        .queryParam("state", stateOrNull)
                        .build(true).toUri();
            }

            RescuedApiResponse resp = rest.getForObject(uri, RescuedApiResponse.class);
            if (resp == null || resp.getResponse() == null || resp.getResponse().getBody() == null) break;
            var body = resp.getResponse().getBody();
            var items = (body.getItems() != null) ? body.getItems().getItem() : null;
            if (items == null || items.isEmpty()) break;

            for (RescuedApiItemDto d : items) {
                Rescued e = d.toEntity(); // 상태/날짜/보호만료 계산 포함

                if (e.getProtectionStatus() == ProtectionStatus.FINISHED) {
                    // 종료건은 삭제
                    affected += rescuedMapper.deleteByDesertionNo(e.getDesertionNo());
                } else {
                    // 보호중인 건만 업서트(상태/이관만 반영)
                    affected += rescuedMapper.upsert(e);
                }
            }

            int total = body.getTotalCount();
            if (total == 0 || page * rows >= total) break;
            page++;
        }
        log.info("Applied careRegNo={}, affected={}", careRegNo, affected);
        return affected;
    }
}
