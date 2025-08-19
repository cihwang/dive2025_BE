package com.example.DIVE2025.domain.rescued.controller;

import com.example.DIVE2025.domain.rescued.dto.RescuedRequestDto;
import com.example.DIVE2025.domain.rescued.dto.RescuedResponseDto;
import com.example.DIVE2025.domain.rescued.service.RescuedQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /**
     * [GET] /api/rescued/animals
     * 보호동물 목록 조회 (보호소/등록번호/필터/페이징/정렬)
     * - q.shelterId가 없으면 JWT에서 추출한 현재 보호소 ID를 자동 사용.
     * - 결과: RescuedResponseDto 리스트
     */
    @GetMapping("/animals")
    public ResponseEntity<List<RescuedResponseDto>> listAnimals(
            RescuedRequestDto q,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long currentShelterId = extractShelterId(jwt);
        List<RescuedResponseDto> list = rescuedQueryService.getAnimals(q, currentShelterId);
        return ResponseEntity.ok(list);
    }

    /**
     * [GET] /api/rescued/animals/count/{shelterId}
     * 특정 보호센터 한 곳의 총계 보여줌
     * - path shelterId가 null일 수 없지만, 서비스 계층은 null 시 JWT 보호소 ID 사용 로직 보유.
     * - 결과: { shelterId, count }
     */
    @GetMapping("/animals/count/{shelterId}")
    public ResponseEntity<Map<String, Object>> countByShelter(
            @PathVariable Long shelterId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long currentShelterId = extractShelterId(jwt);
        long count = rescuedQueryService.countByShelter(shelterId, currentShelterId);
        return ResponseEntity.ok(Map.of("shelterId", shelterId, "count", count));
    }

    /**
     * [GET] /api/rescued/animals/count?careRegNo=...
     * 등록번호(careRegNo) 기준 총 마릿수 카운트
     * - 결과: { careRegNo, count }
     */
    @GetMapping("/animals/count")
    public ResponseEntity<Map<String, Object>> countByCareRegNo(@RequestParam String careRegNo) {
        long count = rescuedQueryService.countByCareRegNo(careRegNo);
        return ResponseEntity.ok(Map.of("careRegNo", careRegNo, "count", count));
    }

    /** 보호소별 동물 카운트 => 전체 보호센터 카운트 반환
     * 대시보드에서 전체 분포볼 때 사용 가능*/
    @GetMapping("/animals/count-group")
    public ResponseEntity<List<HashMap<String, Object>>> countGroupByShelter() {
        return ResponseEntity.ok(rescuedQueryService.countGroupByShelter());
    }

    /**
     * [GET] /api/rescued/animals/count-group/care-reg?shelterId=...
     * 특정 보호소의 등록번호(care_reg_no)별 그룹 집계 => 보호센터 내 지역구별 동물수 집계
     * - shelterId 미지정 시 JWT 보호소 사용.
     * - 결과: [{ careRegNo, count }, ...]
     */
    @GetMapping("/animals/count-group/care-reg")
    public ResponseEntity<List<HashMap<String, Object>>> countByShelterGroupByCareRegNo(
            @RequestParam(required = false) Long shelterId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long currentShelterId = extractShelterId(jwt);
        return ResponseEntity.ok(
                rescuedQueryService.countByShelterGroupByCareRegNo(shelterId, currentShelterId)
        );
    }

    // --------------------- "내 보호센터" 전용 ---------------------

    /**
     * [GET] /api/rescued/myAnimalList
     * 내 보호소의 동물 목록
     * - q.shelterId를 강제로 무시(null) 처리하여 항상 JWT 보호소 기준으로 조회.
     */
    @GetMapping("/myAnimalList")
    public ResponseEntity<List<RescuedResponseDto>> listMine(
            RescuedRequestDto q,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long currentShelterId = extractShelterId(jwt);
        q.setShelterId(null); // 내 보호소만 강제
        List<RescuedResponseDto> list = rescuedQueryService.getAnimals(q, currentShelterId);
        return ResponseEntity.ok(list);
    }

    /**
     * [GET] /api/rescued/myAnimalList/count
     * 내 보호소의 총 마릿수 카운트
     * - 서비스 계층에서 shelterId==null이면 JWT 보호소로 보정.
     * - 결과: { shelterId, count }
     */
    @GetMapping("/myAnimalList/count")
    public ResponseEntity<Map<String, Object>> countMine(@AuthenticationPrincipal Jwt jwt) {
        Long currentShelterId = extractShelterId(jwt);
        long count = rescuedQueryService.countByShelter(null, currentShelterId);
        return ResponseEntity.ok(Map.of("shelterId", currentShelterId, "count", count));
    }

    /**
     * [GET] /api/rescued/myAnimalCount/care-reg
     * 내 보호소의 등록번호(care_reg_no)별 그룹 집계
     * - 결과: [{ careRegNo, count }, ...]
     */
    @GetMapping("/myAnimalCount/care-reg")
    public ResponseEntity<List<HashMap<String, Object>>> countMyShelterByCareRegNo(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long currentShelterId = extractShelterId(jwt);
        return ResponseEntity.ok(
                rescuedQueryService.countByShelterGroupByCareRegNo(null, currentShelterId)
        );
    }

    /**
     * [GET] /api/rescued/myAnimalCount/care-reg
     * 내 보호소의 등록번호(care_reg_no)별 동물 리스트
     */
    @GetMapping("/myAnimalList/care-reg")
    public ResponseEntity<Map<String, List<RescuedResponseDto>>> myAnimalsGrouped(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long shelterId = extractShelterId(jwt);
        return ResponseEntity.ok(rescuedQueryService.getAnimalsGroupedByCareRegNo(shelterId));
    }


    // --------------------- 이관 후보 조회(버튼 토글) ---------------------

    /**
     * [GET] /api/rescued/transfer-candidates
     * 이관 필요 동물 목록 (기간/중증 토글 + 페이징)
     * - usePeriod: 보호기간 임박/경과 필터 사용 (기본 false)
     * - dueWithinDays: n일 내 임박 포함(0이면 경과만)
     * - useSeverity: 중증 & 비병원(GENERAL) 필터 사용 (기본 false)
     * - offset/limit: 페이징 (서비스에서 안전값 보정)
     * - 결과: 우선순 정렬된 RescuedResponseDto 리스트
     */
    @GetMapping("/transfer-candidates")
    public ResponseEntity<List<RescuedResponseDto>> transferCandidates(
            @RequestParam(defaultValue = "false") boolean usePeriod,   // 보호기간 임박/경과 여부
            @RequestParam(defaultValue = "0") int dueWithinDays,       // 0=경과만, n=임박 포함
            @RequestParam(defaultValue = "false") boolean useSeverity, // 질병+보호소 특성 조건
            @RequestParam(defaultValue = "ALL") String condition,      // ALL, MILD, SEVERE
            @RequestParam(required = false) String sort,               // ✅ 추가 (예: daysProtected, overdue, happenDt, rescueDate, age, weight)
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long shelterId = extractShelterId(jwt);
        var list = rescuedQueryService.getTransferCandidates(
                shelterId, usePeriod, dueWithinDays, useSeverity, condition, sort, order, offset, limit
        );
        return ResponseEntity.ok(list);
    }


    // ================== Jwt → shelterId (최소 헬퍼) ==================
    private Long extractShelterId(Jwt jwt) {
        Object sid = jwt.getClaim("shelter_id");
        if (sid == null) sid = jwt.getClaim("shelterId"); // 양쪽 키 지원
        if (sid == null) throw new IllegalStateException("shelter_id claim 없음");
        return Long.valueOf(String.valueOf(sid));
    }
}
