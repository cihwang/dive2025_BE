package com.example.DIVE2025.domain.rescued.controller;

import com.example.DIVE2025.domain.rescued.dto.RescuedRequestDto;
import com.example.DIVE2025.domain.rescued.dto.RescuedResponseDto;
import com.example.DIVE2025.domain.rescued.service.RescuedQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
     * - q.shelterId가 없으면 서비스에서 currentShelterId로 보정.
     * - 결과: RescuedResponseDto 리스트
     */
    @GetMapping("/animals")
    public ResponseEntity<List<RescuedResponseDto>> listAnimals(
            RescuedRequestDto q,
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        List<RescuedResponseDto> list = rescuedQueryService.getAnimals(q, currentShelterId);
        return ResponseEntity.ok(list);
    }

    /**
     * [GET] /api/rescued/animals/count/{shelterId}
     * 특정 보호센터 한 곳의 총계
     * - 서비스 계층은 shelterId==null이면 currentShelterId 사용 로직 보유.
     * - 결과: { shelterId, count }
     */
    @GetMapping("/animals/countByShelterId/{shelterId}")
    public ResponseEntity<Map<String, Object>> countByShelter(
            @PathVariable Long shelterId,
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        long count = rescuedQueryService.countByShelter(shelterId, currentShelterId);
        Long keyShelterId = (shelterId != null) ? shelterId : currentShelterId;
        return ResponseEntity.ok(Map.of("shelterId", keyShelterId, "count", count));
    }

    /**
     * [GET] /api/rescued/animals/count?careRegNo=...
     * 등록번호(careRegNo) 기준 총 마릿수 카운트
     * - 결과: { careRegNo, count }
     */
    @GetMapping("/animals/countByCareRegNo")
    public ResponseEntity<Map<String, Object>> countByCareRegNo(@RequestParam String careRegNo) {
        long count = rescuedQueryService.countByCareRegNo(careRegNo);
        return ResponseEntity.ok(Map.of("careRegNo", careRegNo, "count", count));
    }

    /**
     * [GET] /api/rescued/animals/count-group
     * 보호소별 동물 카운트 (대시보드 분포)
     */
    @GetMapping("/animals/count-group")
    public ResponseEntity<List<HashMap<String, Object>>> countGroupByShelter() {
        return ResponseEntity.ok(rescuedQueryService.countGroupByShelter());
    }

    /**
     * [GET] /api/rescued/animals/count-group/care-reg?shelterId=...
     * 특정 보호소의 등록번호(care_reg_no)별 그룹 집계
     * - shelterId 미지정 시 서비스에서 currentShelterId 사용.
     * - 결과: [{ careRegNo, count }, ...]
     */
    @GetMapping("/animals/count-group/care-reg")
    public ResponseEntity<List<HashMap<String, Object>>> countByShelterGroupByCareRegNo(
            @RequestParam(required = false) Long shelterId,
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        return ResponseEntity.ok(
                rescuedQueryService.countByShelterGroupByCareRegNo(shelterId, currentShelterId)
        );
    }

    // --------------------- "내 보호센터" 전용 ---------------------

    /**
     * [GET] /api/rescued/myAnimalList
     * 내 보호소의 동물 목록 (항상 JWT 보호소 기준)
     */
    @GetMapping("/myAnimalList")
    public ResponseEntity<List<RescuedResponseDto>> listMine(
            RescuedRequestDto q,
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        q.setShelterId(null); // 내 보호소만 강제
        List<RescuedResponseDto> list = rescuedQueryService.getAnimals(q, currentShelterId);
        return ResponseEntity.ok(list);
    }

    /**
     * [GET] /api/rescued/myAnimalList/count
     * 내 보호소의 총 마릿수 카운트
     * - 결과: { shelterId, count }
     */
    @GetMapping("/myAnimalList/count")
    public ResponseEntity<Map<String, Object>> countMine(
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        long count = rescuedQueryService.countByShelter(null, currentShelterId);
        return ResponseEntity.ok(Map.of("shelterId", currentShelterId, "count", count));
    }

    /**
     * [GET] /api/rescued/myAnimalCount/care-reg
     * 내 보호소의 등록번호(care_reg_no)별 그룹 집계
     */
    @GetMapping("/myAnimalCount/care-reg")
    public ResponseEntity<List<HashMap<String, Object>>> countMyShelterByCareRegNo(
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        return ResponseEntity.ok(
                rescuedQueryService.countByShelterGroupByCareRegNo(null, currentShelterId)
        );
    }

    /**
     * [GET] /api/rescued/myAnimalList/care-reg
     * 내 보호소의 등록번호(care_reg_no)별 동물 리스트
     */
    @GetMapping("/myAnimalList/care-reg")
    public ResponseEntity<Map<String, List<RescuedResponseDto>>> myAnimalsGrouped(
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        return ResponseEntity.ok(rescuedQueryService.getAnimalsGroupedByCareRegNo(currentShelterId));
    }

    // --------------------- 이관 후보 조회(버튼 토글) ---------------------

    /**
     * [GET] /api/rescued/transfer-candidates
     * 이관 필요 동물 목록 (기간/중증 토글 + 페이징)
     * - usePeriod: 보호기간 임박/경과 필터 사용 (기본 false)
     * - dueWithinDays: n일 내 임박 포함(0이면 경과만)
     * - useSeverity: 중증 & 비병원(GENERAL) 필터 (기본 false)
     * - sort: 예) daysProtected, overdue, happenDt, rescueDate, age, weight
     * - order: asc|desc
     * - offset/limit: 페이징
     */
    @GetMapping("/transfer-candidates")
    public ResponseEntity<List<RescuedResponseDto>> transferCandidates(
            @RequestParam(defaultValue = "false") boolean usePeriod,
            @RequestParam(defaultValue = "0") int dueWithinDays,
            @RequestParam(defaultValue = "false") boolean useSeverity,
            @RequestParam(defaultValue = "ALL") String condition,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        var list = rescuedQueryService.getTransferCandidates(
                currentShelterId, usePeriod, dueWithinDays, useSeverity, condition, sort, order, offset, limit
        );
        return ResponseEntity.ok(list);
    }
}
