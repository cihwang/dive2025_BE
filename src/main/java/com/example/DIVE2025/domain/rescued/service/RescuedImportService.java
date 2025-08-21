package com.example.DIVE2025.domain.rescued.service;

import com.example.DIVE2025.domain.rescued.ai.SpecialMarkAiClient;
import com.example.DIVE2025.domain.rescued.dto.RescuedApiItemDto;
import com.example.DIVE2025.domain.rescued.dto.RescuedApiResponse;
import com.example.DIVE2025.domain.rescued.dto.RescuedResponseDto;
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
import org.springframework.web.client.ResourceAccessException;
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
    @Value("${external.abandon.rows:200}") private int rows;   // ✅ 기본 200으로 줄임
    @Value("${ai.analyzer.enabled:true}") private boolean aiEnabled;

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
            changed += fetchAndApply(start, end, reg, "protect");
        }
        log.info("Initial build changed rows={}", changed);
        return changed;
    }

    /** 2) 정기 동기화: 최근 N일 */
    public int syncRecentDays(int recentDays) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(recentDays);
        List<String> regs = rescuedMapper.selectAllCareRegNos();
        int changed = 0;
        for (String reg : regs) {
            changed += fetchAndApplyChunked(start, end, reg, null, 1); // 하루 단위 쪼개기
        }
        log.info("Sync changed rows={}", changed);
        return changed;
    }

    /** 공통 fetch + 적용 (state가 null이면 파라미터 생략) */
    private int fetchAndApply(LocalDate start, LocalDate end, String careRegNo, String stateOrNull) {
        int affected = 0;
        int page = 1;

        for (;;) {
            String rawKey = serviceKey == null ? "" : serviceKey.trim();
            String encodedKey = URLEncoder.encode(rawKey, StandardCharsets.UTF_8);

            UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
                    .queryParam("serviceKey", encodedKey)
                    .queryParam("care_reg_no", careRegNo)
                    .queryParam("bgnde", start.format(API_FMT))
                    .queryParam("endde", end.format(API_FMT))
                    .queryParam("pageNo", page)
                    .queryParam("numOfRows", rows)
                    .queryParam("_type", "json");

            if (stateOrNull != null) b.queryParam("state", stateOrNull);
            URI uri = b.build(true).toUri();

            boolean success = false;
            int retries = 0;

            while (!success && retries < 3) {
                try {
                    log.info("CALL {} (careRegNo={}, page={}, try={})",
                            maskKey(uri.toString()), careRegNo, page, retries+1);

                    var headers = new HttpHeaders();
                    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                    var req = new HttpEntity<String>(headers);

                    var respEntity = rest.exchange(uri, HttpMethod.GET, req, String.class);
                    String raw = respEntity.getBody();
                    if (raw == null || raw.isBlank()) { log.warn("Empty body"); break; }
                    if (raw.startsWith("<")) { log.warn("Non-JSON response head={}", raw.substring(0,100)); break; }

                    RescuedApiResponse api = lenientObjectMapper.readValue(raw, RescuedApiResponse.class);
                    var respBody = (api!=null && api.getResponse()!=null) ? api.getResponse().getBody() : null;
                    var items = (respBody!=null && respBody.getItems()!=null) ? respBody.getItems().getItem() : null;
                    if (items == null || items.isEmpty()) { break; }

                    // === DB 반영 ===
                    for (RescuedApiItemDto d : items) {
                        var e = d.toEntity();
                        String specialMark = d.getSpecialMark();

                        if (aiEnabled && e.getProtectionStatus() != ProtectionStatus.FINISHED) {
                            try {
                                var cond = specialMarkAiClient.analyze(specialMark);
                                e.setAnimalCondition(cond != null ? cond :
                                        com.example.DIVE2025.domain.rescued.enums.AnimalCondition.NORMAL);
                            } catch (Exception ex) {
                                e.setAnimalCondition(com.example.DIVE2025.domain.rescued.enums.AnimalCondition.NORMAL);
                            }
                        } else {
                            e.setAnimalCondition(com.example.DIVE2025.domain.rescued.enums.AnimalCondition.NORMAL);
                        }

                        int n = (e.getProtectionStatus() == ProtectionStatus.FINISHED)
                                ? rescuedMapper.deleteByDesertionNo(e.getDesertionNo())
                                : rescuedMapper.upsert(e);

                        RescuedResponseDto shelterIdByDesertionNo =
                                rescuedMapper.getShelterIdByDesertionNo(e.getDesertionNo());

                        if (e.getProtectionStatus() != ProtectionStatus.FINISHED) {
                            fileUploadUtil.uploadImageFromUrl(
                                    e.getPopfile1(), shelterIdByDesertionNo.getShelterId(), e.getDesertionNo());
                        }
                        affected += n;
                    }

                    int total = (respBody != null) ? respBody.getTotalCount() : 0;
                    if (total == 0 || page * rows >= total) success = true;
                    else page++;

                } catch (ResourceAccessException ex) {
                    retries++;
                    int backoff = (int) Math.pow(2, retries); // 2, 4, 8초
                    log.warn("Timeout careRegNo={}, page={}, retry {}/3 after {}s",
                            careRegNo, page, retries, backoff);
                    try { Thread.sleep(backoff * 1000L); } catch (InterruptedException ignore) {}
                } catch (Exception ex) {
                    log.error("Fetch/apply failed careRegNo={}, page={}, uri={}",
                            careRegNo, page, maskKey(uri.toString()), ex);
                    break;
                }
            }
            if (!success) break;
        }
        log.info("Applied careRegNo={}, affected={}", careRegNo, affected);
        return affected;
    }

    /** serviceKey 마스킹 */
    private static String maskKey(String raw) {
        return raw.replaceAll("(serviceKey=)([^&]+)", "$1***");
    }

    // 테스트용 훅
    public int rescuedImportService_testHook(LocalDate start, LocalDate end,
                                             String careRegNo, String state) {
        return fetchAndApply(start, end, careRegNo, state);
    }

    /** 전체 보호소 import */
    public Map<String, Object> importAll(LocalDate start, LocalDate end, String stateOrNull, int windowDays) {
        List<String> careRegNos = rescuedMapper.selectAllCareRegNos();
        return importForList(careRegNos, start, end, stateOrNull, windowDays);
    }

    /** 여러 보호소 import */
    public Map<String, Object> importForList(List<String> careRegNos, LocalDate start, LocalDate end,
                                             String stateOrNull, int windowDays) {
        Map<String, Integer> byShelter = new LinkedHashMap<>();
        int total = 0;

        for (String careRegNo : careRegNos) {
            int changed;
            try {
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

    /** 단일 보호소 import */
    public int importOne(String careRegNo, LocalDate start, LocalDate end,
                         String stateOrNull, int windowDays) {
        return fetchAndApplyChunked(start, end, careRegNo, stateOrNull, windowDays);
    }

    /** 기간 분할 실행 */
    private int fetchAndApplyChunked(LocalDate start, LocalDate end,
                                     String careRegNo, String stateOrNull, int windowDays) {
        int affected = 0;
        LocalDate s = start;
        while (!s.isAfter(end)) {
            LocalDate e = s.plusDays(windowDays - 1);
            if (e.isAfter(end)) e = end;
            affected += fetchAndApply(s, e, careRegNo, stateOrNull);
            s = e.plusDays(1);
            try { Thread.sleep(80L); } catch (InterruptedException ignore) {}
        }
        return affected;
    }
}
