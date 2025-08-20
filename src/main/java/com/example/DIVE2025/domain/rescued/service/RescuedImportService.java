package com.example.DIVE2025.domain.rescued.service;


import com.example.DIVE2025.domain.rescued.ai.SpecialMarkAiClient;
import com.example.DIVE2025.domain.rescued.dto.RescuedApiItemDto;
import com.example.DIVE2025.domain.rescued.dto.RescuedApiResponse;
import com.example.DIVE2025.domain.rescued.dto.RescuedResponseDto;
import com.example.DIVE2025.domain.rescued.entity.Rescued;
import com.example.DIVE2025.domain.rescued.enums.ProtectionStatus;
import com.example.DIVE2025.domain.rescued.mapper.RescuedMapper;
import com.example.DIVE2025.domain.rescued.util.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RescuedImportService {

    private final RescuedMapper rescuedMapper;
    private final RestTemplate rest;
    private final ObjectMapper lenientObjectMapper;
    private final FileUploadUtil fileUploadUtil;
    private final SpecialMarkAiClient specialMarkAiClient;


    @Value("${external.abandon.base-url}") private String baseUrl;
    @Value("${external.abandon.path}") private String path;
    @Value("${external.abandon.service-key}") private String serviceKey;
    @Value("${external.abandon.rows:1000}") private int rows;
    @Value("${ai.analyzer.enabled:true}")
    private boolean aiEnabled;

    private static final DateTimeFormatter API_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    public RescuedImportService(RescuedMapper rescuedMapper, RestTemplate rest,
                                ObjectMapper lenientObjectMapper,
                                FileUploadUtil fileUploadUtil,
                                SpecialMarkAiClient specialMarkAiClient) {
        this.rescuedMapper = rescuedMapper;
        this.rest = rest;
        this.lenientObjectMapper = lenientObjectMapper;
        this.fileUploadUtil = fileUploadUtil;
        this.specialMarkAiClient = specialMarkAiClient;
    }

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
            // 1) 서비스키 정리: null/공백 방지 + 단일 인코딩
            String rawKey = serviceKey == null ? "" : serviceKey.trim();
            String encodedKey = URLEncoder.encode(rawKey, StandardCharsets.UTF_8);

            UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
                    .queryParam("serviceKey", encodedKey) // 이미 인코딩된 값
                    .queryParam("care_reg_no", careRegNo)
                    .queryParam("bgnde", start.format(API_FMT))
                    .queryParam("endde", end.format(API_FMT))
                    .queryParam("pageNo", page)
                    .queryParam("numOfRows", rows)
                    .queryParam("_type", "json");

            if (stateOrNull != null) b.queryParam("state", stateOrNull);

            // ★ 전체 URI는 재인코딩 금지
            URI uri = b.build(true).toUri();

            try {
                log.info("CALL {} (careRegNo={}, page={}, key.len={})",
                        maskKey(uri.toString()), careRegNo, page, rawKey.length());

                var headers = new HttpHeaders();
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                var req = new HttpEntity<String>(headers);

                var respEntity = rest.exchange(uri, HttpMethod.GET, req, String.class);
                log.info("RESP status={} headers={}", respEntity.getStatusCode(), respEntity.getHeaders());

                String raw = respEntity.getBody();
                if (raw == null || raw.isBlank()) { log.warn("Empty body"); break; }

                String head = raw.substring(0, Math.min(400, raw.length())).replaceAll("\\s+"," ");
                if (head.startsWith("<")) { // XML/HTML = 오류 응답
                    log.warn("Non-JSON (likely auth/quota). head={}", head);
                    break;
                }

                RescuedApiResponse api = lenientObjectMapper.readValue(raw, RescuedApiResponse.class);
                var header = (api!=null && api.getResponse()!=null) ? api.getResponse().getHeader() : null;
                if (header != null && !"00".equals(header.getResultCode())) {
                    log.warn("API error resultCode={}, msg={}", header.getResultCode(), header.getResultMsg());
                    break;
                }

                var respBody = api.getResponse().getBody();
                var items = (respBody!=null && respBody.getItems()!=null) ? respBody.getItems().getItem() : null;
                if (items == null || items.isEmpty()) { log.info("No items (page={})", page); break; }

                for (RescuedApiItemDto d : items) {
                    var e = d.toEntity();

                    String specialMark = null;
                    try{
                        specialMark = d.getSpecialMark();
                    }catch(Exception ignore){
                        specialMark = null;
                    }

                    if (aiEnabled && e.getProtectionStatus() != ProtectionStatus.FINISHED) {
                        try {
                            var cond = specialMarkAiClient.analyze(specialMark);
                            e.setAnimalCondition(cond != null ? cond : com.example.DIVE2025.domain.rescued.enums.AnimalCondition.NORMAL);
                        } catch (Exception ex) {
                            // 실패 시 안전 기본값
                            e.setAnimalCondition(com.example.DIVE2025.domain.rescued.enums.AnimalCondition.NORMAL);
                        }
                    } else {
                        // AI 비활성 또는 FINISHED면 NORMAL로 통일
                        e.setAnimalCondition(com.example.DIVE2025.domain.rescued.enums.AnimalCondition.NORMAL);
                    }


                    int n = (e.getProtectionStatus()==ProtectionStatus.FINISHED)
                            ? rescuedMapper.deleteByDesertionNo(e.getDesertionNo())
                            : rescuedMapper.upsert(e);

                    RescuedResponseDto shelterIdByDesertionNo = rescuedMapper.getShelterIdByDesertionNo(e.getDesertionNo());

                    if(e.getProtectionStatus()!=ProtectionStatus.FINISHED) {
                        fileUploadUtil.uploadImageFromUrl(e.getPopfile1(), shelterIdByDesertionNo.getShelterId(), e.getDesertionNo());
                    }

                    affected += n;
                    log.debug("{} desertionNo={} -> affected={}",
                            (e.getProtectionStatus()==ProtectionStatus.FINISHED) ? "DEL" : "UPSERT",
                            e.getDesertionNo(), n);
                }

                int total = (respBody != null) ? respBody.getTotalCount() : 0;
                log.info("Fetched items={}, total={}, page={}, rows={}",
                        (items!=null?items.size():0), total, page, rows);

                if (total == 0 || page * rows >= total) break;
                page++;

            } catch (Exception ex) {
                log.error("Fetch/apply failed careRegNo={}, page={}, uri={}",
                        careRegNo, page, maskKey(uri.toString()), ex);
                break;
            }
        }
        log.info("Applied careRegNo={}, affected={}", careRegNo, affected);
        return affected;
    }


    /** serviceKey 마스킹 */
    private static String maskKey(String raw) {
        return raw.replaceAll("(serviceKey=)([^&]+)", "$1***");
    }

    // RescuedImportService - 임시 훅(테스트 후 삭제)
    public int rescuedImportService_testHook(LocalDate start, LocalDate end,
                                             String careRegNo, String state) {
        return fetchAndApply(start, end, careRegNo, state);
    }



    public Map<String, Object> importAll(LocalDate start, LocalDate end, String stateOrNull, int windowDays) {
        List<String> careRegNos = rescuedMapper.selectAllCareRegNos();
        return importForList(careRegNos, start, end, stateOrNull, windowDays);
    }

    public Map<String, Object> importForList(List<String> careRegNos, LocalDate start, LocalDate end,
                                             String stateOrNull, int windowDays) {
        Map<String, Integer> byShelter = new LinkedHashMap<>();
        int total = 0;

        for (String careRegNo : careRegNos) {
            int changed;
            try {
                // 선택: shelter_registration 매핑 없으면 스킵
                if (!rescuedMapper.existsShelterMapping(careRegNo)) {
                    log.warn("skip import (no shelter_registration mapping) careRegNo={}", careRegNo);
                    byShelter.put(careRegNo, -2);
                    continue;
                }
                changed = importOne(careRegNo, start, end, stateOrNull, windowDays);
            } catch (Exception ex) {
                log.error("importForList failed careRegNo={}", careRegNo, ex);
                changed = -1;
            }
            byShelter.put(careRegNo, changed);
            if (changed > 0) total += changed;

            // 공공API 배려 (QPS)
            try { Thread.sleep(120L); } catch (InterruptedException ignore) {}
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("from", start.toString());
        out.put("to", end.toString());
        out.put("state", stateOrNull);
        out.put("windowDays", windowDays);
        out.put("shelterCount", careRegNos.size());
        out.put("totalChanged", total);
        out.put("byShelter", byShelter);
        return out;
    }

    public int importOne(String careRegNo, LocalDate start, LocalDate end,
                         String stateOrNull, int windowDays) {
        return fetchAndApplyChunked(start, end, careRegNo, stateOrNull, windowDays);
    }

    private int fetchAndApplyChunked(LocalDate start, LocalDate end,
                                     String careRegNo, String stateOrNull, int windowDays) {
        int affected = 0;
        LocalDate s = start;
        while (!s.isAfter(end)) {
            LocalDate e = s.plusDays(windowDays - 1);
            if (e.isAfter(end)) e = end;
            affected += fetchAndApply(s, e, careRegNo, stateOrNull); // 기존 페이지네이션 포함 메서드 재사용
            s = e.plusDays(1);
            try { Thread.sleep(80L); } catch (InterruptedException ignore) {}
        }
        return affected;
    }



}
