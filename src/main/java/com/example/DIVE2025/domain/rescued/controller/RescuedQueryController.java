package com.example.DIVE2025.domain.rescued.controller;

import com.example.DIVE2025.domain.rescued.dto.RescuedApiResponse;
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

    /** 현재 부산시 전체 보호동물 목록 조회 */
    @GetMapping("/animals")
    public ResponseEntity<List<RescuedResponseDto>> listAnimals(
            RescuedRequestDto q,
            // Principal에 shelterId 속성이 있을 때 자동 주입 (없으면 null)
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        List<RescuedResponseDto> list = rescuedQueryService.getAnimals(q, currentShelterId);
        return ResponseEntity.ok(list);
    }

    /** 특정 보호소(PK) 총 카운트 */
    @GetMapping("/animals/count/{shelterId}")
    public ResponseEntity<Map<String, Object>> countByShelter(
            @PathVariable(required = false) Long shelterId,
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        long count = rescuedQueryService.countByShelter(shelterId, currentShelterId);
        return ResponseEntity.ok(Map.of("shelterId", shelterId != null ? shelterId : currentShelterId, "count", count));
    }

    /** 특정 등록번호(careRegNo) 총 카운트 */
    @GetMapping("/animals/count")
    public ResponseEntity<Map<String, Object>> countByCareRegNo(
            @RequestParam String careRegNo
    ) {
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
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        return ResponseEntity.ok(rescuedQueryService.countByShelterGroupByCareRegNo(shelterId, currentShelterId));
    }


//    내 보호센터의 동물 리스트업
    @GetMapping("/myAnimalList")
    public ResponseEntity<List<RescuedResponseDto>> listMine
            (RescuedRequestDto q, @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId) {
        q.setShelterId(null);
        List<RescuedResponseDto> list = rescuedQueryService.getAnimals(q, currentShelterId);
        return ResponseEntity.ok(list);
    }


//    내 보호센터의 동물 수 카운트
    @GetMapping("/myAnimalList/count")
    public ResponseEntity<Map<String, Object>> countMine(@AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        long count = rescuedQueryService.countByShelter(null, currentShelterId);
        return ResponseEntity.ok(Map.of("shelterId", currentShelterId, "count", count));
    }

//    내 보호센터 동물 수 지역구별 카운트
    @GetMapping("/myAnimalCount/care-reg")
    public ResponseEntity<List<HashMap<String, Object>>> countMyShelterByCareRegNo(
            @AuthenticationPrincipal(expression = "shelterId") Long currentShelterId
    ) {
        return ResponseEntity.ok(rescuedQueryService.countByShelterGroupByCareRegNo(null, currentShelterId));
    }


}
