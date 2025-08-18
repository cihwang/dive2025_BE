package com.example.DIVE2025.domain.rescued.ai;

import com.example.DIVE2025.domain.rescued.enums.AnimalCondition; // ★ 여기만 바뀌면 됨
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiSpecialMarkAiClient implements SpecialMarkAiClient {

    private final RestTemplate restTemplate;

    @Value("${ai.gemini.api-key:}")
    private String apiKey;

    @Value("${ai.gemini.model:gemini-1.5-flash}")
    private String model;

    @Override
    public AnimalCondition analyze(String specialMark) {
        if (specialMark == null || specialMark.isBlank() || apiKey == null || apiKey.isBlank()) {
            return heuristic(specialMark);
        }

        String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model, apiKey
        );

        String prompt =
                "You are a veterinary triage assistant for Korean shelter notes.\n" +
                        "Map the text to exactly one of: SEVERE, MILD, NORMAL.\n" +
                        "- SEVERE: life-threatening or requires urgent surgery (e.g., parvo/distemper, respiratory distress, " +
                        "  sepsis, severe bleeding, multiple fractures, open fractures).\n" +
                        "- MILD: minor/moderate issues (e.g., mild skin/eye disease, small wound) not immediately critical.\n" +
                        "- NORMAL: no specific abnormal findings.\n" +
                        "Return ONLY the label (no explanation).\n" +
                        "Text:\n" + specialMark;

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.postForObject(url, req, Map.class);

            String text = extractText(resp);
            AnimalCondition label = toLabel(text);
            return postprocessWithLexicon(specialMark, label);

        } catch (Exception e) {
            // 모델 호출 실패해도 용어사전으로 먼저 보정, 그래도 없으면 NORMAL
            return postprocessWithLexicon(specialMark, null);
        }

    }

    private String extractText(Map<String, Object> resp) {
        if (resp == null) return "";
        Object cands = resp.get("candidates");
        if (!(cands instanceof List<?> list) || list.isEmpty()) return "";
        Object content = ((Map<?, ?>) list.get(0)).get("content");
        if (!(content instanceof Map<?, ?> cMap)) return "";
        Object parts = cMap.get("parts");
        if (!(parts instanceof List<?> pList) || pList.isEmpty()) return "";
        Object text = ((Map<?, ?>) pList.get(0)).get("text");
        return text == null ? "" : text.toString().trim();
    }


    private AnimalCondition toLabel(String raw) {
        if (raw == null) return AnimalCondition.NORMAL;
        String v = raw.trim().toUpperCase(Locale.ROOT);
        int sp = v.indexOf(' ');
        if (sp > 0) v = v.substring(0, sp);

        if (v.startsWith("SEVERE")) return AnimalCondition.SEVERE;
        if (v.startsWith("MILD"))   return AnimalCondition.MILD;
        if (v.startsWith("NORMAL")) return AnimalCondition.NORMAL;
        return AnimalCondition.NORMAL;
    }

    /** 한글 키워드 기반 폴백(간단/보수적) */
    private AnimalCondition heuristic(String text) {
        if (text == null) return AnimalCondition.NORMAL;
        String t = text.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);

        // 중증 키워드
        if (t.matches(".*(파보|홍역|parvo|distemper|호흡곤란|호흡부전|다발성골절|골절|개방성골절|대량출혈|출혈|패혈|sepsis|쇼크|shock|탈수심함|응급수술|응급).*")) {
            return AnimalCondition.SEVERE;
        }
        // 경증 키워드
        if (t.matches(".*(피부염|결막염|경미|가벼운|표재성상처|긁힘|가벼운기침|콧물|mild).*")) {
            return AnimalCondition.MILD;
        }
        // 특이사항 없음/정상
        if (t.matches(".*(특이사항없음|이상없음|정상|양호|괜찮음|문제없음|normal|healthy).*")) {
            return AnimalCondition.NORMAL;
        }
        // 모호하면 보수적으로 NORMAL
        return AnimalCondition.NORMAL;
    }

    private String normalize(String s) {
        return s == null ? "" : s.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }
    private boolean containsAny(String text, List<String> terms) {
        String t = normalize(text);
        for (String term : terms) {
            if (t.contains(normalize(term))) return true;
        }
        return false;
    }

    private AnimalCondition postprocessWithLexicon(String original, AnimalCondition modelLabel) {
        if (original != null) {
            if (containsAny(original, AiLexicon.SEVERE_TERMS)) return AnimalCondition.SEVERE;
            if (containsAny(original, AiLexicon.MILD_TERMS))   return AnimalCondition.MILD;
            if (containsAny(original, AiLexicon.NORMAL_TERMS)) return AnimalCondition.NORMAL;
        }
        // 사전에 안 걸리면 모델 라벨 사용, 그래도 없으면 NORMAL
        return modelLabel != null ? modelLabel : AnimalCondition.NORMAL;
    }


}
