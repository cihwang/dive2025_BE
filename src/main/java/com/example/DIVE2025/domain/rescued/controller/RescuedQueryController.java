package com.example.DIVE2025.domain.rescued.controller;

import com.example.DIVE2025.domain.rescued.dto.RescuedRequestDto;
import com.example.DIVE2025.domain.rescued.dto.RescuedResponseDto;
import com.example.DIVE2025.domain.rescued.service.RescuedQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rescued")
public class RescuedQueryController {

    private final RescuedQueryService rescuedQueryService;

    /** 현재 부산시 전체 보호동물 목록 조회 */
    @GetMapping("/animals")
    public ResponseEntity<List<RescuedResponseDto>> listAnimals(
            RescuedRequestDto q,
            @AuthenticationPrincipal Object principal
    ) {
        Long currentShelterId = resolveShelterId(principal);
        List<RescuedResponseDto> list = rescuedQueryService.getAnimals(q, currentShelterId);
        return ResponseEntity.ok(list);
    }

    /** 특정 보호소(PK) 총 카운트 */
    @GetMapping("/animals/count/{shelterId}")
    public ResponseEntity<Map<String, Object>> countByShelter(
            @PathVariable(required = false) Long shelterId,
            @AuthenticationPrincipal Object principal
    ) {
        Long currentShelterId = resolveShelterId(principal);
        long count = rescuedQueryService.countByShelter(shelterId, currentShelterId);
        return ResponseEntity.ok(Map.of("shelterId", shelterId != null ? shelterId : currentShelterId, "count", count));
    }

    /** 특정 등록번호(careRegNo) 총 카운트 */
    @GetMapping("/animals/count")
    public ResponseEntity<Map<String, Object>> countByCareRegNo(@RequestParam String careRegNo) {
        long count = rescuedQueryService.countByCareRegNo(careRegNo);
        return ResponseEntity.ok(Map.of("careRegNo", careRegNo, "count", count));
    }

    /** 보호소별 그룹 카운트 (전체) */
    @GetMapping("/animals/count-group")
    public ResponseEntity<List<HashMap<String, Object>>> countGroupByShelter() {
        return ResponseEntity.ok(rescuedQueryService.countGroupByShelter());
    }

    /** 특정 보호소의 지역구(care_reg_no)별 그룹 카운트 */
    @GetMapping("/animals/count-group/care-reg")
    public ResponseEntity<List<HashMap<String, Object>>> countByShelterGroupByCareRegNo(
            @RequestParam(required = false) Long shelterId,
            @AuthenticationPrincipal Object principal
    ) {
        Long currentShelterId = resolveShelterId(principal);
        return ResponseEntity.ok(rescuedQueryService.countByShelterGroupByCareRegNo(shelterId, currentShelterId));
    }

    // --------------------- "내 보호센터" 전용 ---------------------

    /** 내 보호센터의 동물 리스트업 */
    @GetMapping("/myAnimalList")
    public ResponseEntity<List<RescuedResponseDto>> listMine(
            RescuedRequestDto q,
            @AuthenticationPrincipal Object principal
    ) {
        Long currentShelterId = resolveShelterId(principal);
        q.setShelterId(null); // 내 보호소만 강제
        List<RescuedResponseDto> list = rescuedQueryService.getAnimals(q, currentShelterId);
        return ResponseEntity.ok(list);
    }

    /** 내 보호센터의 동물 수 카운트 */
    @GetMapping("/myAnimalList/count")
    public ResponseEntity<Map<String, Object>> countMine(@AuthenticationPrincipal Object principal) {
        Long currentShelterId = resolveShelterId(principal);
        long count = rescuedQueryService.countByShelter(null, currentShelterId);
        return ResponseEntity.ok(Map.of("shelterId", currentShelterId, "count", count));
    }

    /** 내 보호센터 동물 수 지역구별 카운트 */
    @GetMapping("/myAnimalCount/care-reg")
    public ResponseEntity<List<HashMap<String, Object>>> countMyShelterByCareRegNo(
            @AuthenticationPrincipal Object principal
    ) {
        Long currentShelterId = resolveShelterId(principal);
        return ResponseEntity.ok(rescuedQueryService.countByShelterGroupByCareRegNo(null, currentShelterId));
    }

    // ================== 헬퍼: principal → shelterId ==================
    private Long resolveShelterId(Object principal) {
        if (principal == null) return null;

        // 1) JWT(Resource Server 또는 커스텀)인 경우
        if (principal instanceof Jwt jwt) {
            return toLong(jwt.getClaim("shelterId"), jwt.getClaim("shelter_id"));
        }
        // 2) OIDC / OAuth2 로그인
        if (principal instanceof OidcUser oidc) {
            Map<String, Object> a = oidc.getAttributes();
            return toLong(a.get("shelterId"), a.get("shelter_id"));
        }
        if (principal instanceof OAuth2User oAuth2User) {
            Map<String, Object> a = oAuth2User.getAttributes();
            return toLong(a.get("shelterId"), a.get("shelter_id"));
        }
        // 3) 커스텀 Principal (getShelterId 존재 시)
        try {
            var m = principal.getClass().getMethod("getShelterId");
            Object v = m.invoke(principal);
            return toLong(v);
        } catch (Exception ignore) {}

        // 4) Map 형태
        if (principal instanceof Map<?, ?> map) {
            return toLong(map.get("shelterId"), map.get("shelter_id"));
        }

        // 5) 그 외(String username 등) → 없음
        return null;
    }

    private Long toLong(Object... candidates) {
        for (Object v : candidates) {
            if (v == null) continue;
            if (v instanceof Number n) return n.longValue();
            try { return Long.parseLong(v.toString()); } catch (Exception ignored) {}
        }
        return null;
    }
}
