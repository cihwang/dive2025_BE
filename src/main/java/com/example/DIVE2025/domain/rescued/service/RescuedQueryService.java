package com.example.DIVE2025.domain.rescued.service;

import com.example.DIVE2025.domain.rescued.dto.RescuedRequestDto;
import com.example.DIVE2025.domain.rescued.dto.RescuedResponseDto;
import com.example.DIVE2025.domain.rescued.mapper.RescuedQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;


//구조동물 조회용 서비스
@Service
@RequiredArgsConstructor
public class RescuedQueryService {

    private final RescuedQueryMapper rescuedQueryMapper;

    public List<RescuedResponseDto> getAnimals(RescuedRequestDto q, Long currentShelterIdOrNull) {
        // 1) shelterId 자동 주입 (로그인 사용자의 보호소)
        if (q.getShelterId() == null && currentShelterIdOrNull != null) {
            q.setShelterId(currentShelterIdOrNull);
        }

        // 2) 최소 식별 검증: shelterId 또는 careRegNo / careRegNos 중 하나는 있어야 한다
        boolean hasShelter = q.getShelterId() != null;
        boolean hasCareRegNo = StringUtils.hasText(q.getCareRegNo());
        boolean hasCareRegNos = !CollectionUtils.isEmpty(q.getCareRegNos());

        if (!hasShelter && !hasCareRegNo && !hasCareRegNos) {
            throw new IllegalArgumentException("shelterId 또는 careRegNo(또는 careRegNos) 중 하나는 반드시 필요합니다.");
        }

        // 3) 중복된 careRegNos 정리 (있다면)
        if (hasCareRegNos) {
            Set<String> dedup = new HashSet<>(q.getCareRegNos());
            q.setCareRegNos(dedup.stream().toList());
        }

        // 4) 페이징/정렬 기본값 보정
        if (q.getPage() == null || q.getPage() < 0) q.setPage(0);
        if (q.getSize() == null || q.getSize() <= 0) q.setSize(20);

        // sort 화이트리스트
        Set<String> allowedSorts = Set.of("happenDt", "rescueDate", "age", "weight");
        if (!allowedSorts.contains(q.getSort())) {
            q.setSort("rescueDate"); // 기본
        }
        // order 화이트리스트
        if (!"asc".equalsIgnoreCase(q.getOrder())) {
            q.setOrder("desc"); // 기본
        }

        return rescuedQueryMapper.findAnimalsByShelter(q);
        // needsTransfer는 2번 기능에서 규칙 주입 예정
    }

    public long countByShelter(Long shelterId, Long currentShelterIdOrNull) {
        Long effective = (shelterId != null) ? shelterId : currentShelterIdOrNull;
        if (effective == null) {
            throw new IllegalArgumentException("shelterId가 필요합니다.");
        }
        return rescuedQueryMapper.countByShelter(effective);
    }

    public long countByCareRegNo(String careRegNo) {
        if (!StringUtils.hasText(careRegNo)) {
            throw new IllegalArgumentException("careRegNo가 필요합니다.");
        }
        return rescuedQueryMapper.countByCareRegNo(careRegNo);
    }

    public List<java.util.HashMap<String, Object>> countGroupByShelter() {
        return rescuedQueryMapper.countGroupByShelter();
    }

    public List<java.util.HashMap<String, Object>> countByShelterGroupByCareRegNo(Long shelterId, Long currentShelterIdOrNull) {
        Long effective = (shelterId != null) ? shelterId : currentShelterIdOrNull;
        if (effective == null) {
            throw new IllegalArgumentException("shelterId가 필요합니다.");
        }
        return rescuedQueryMapper.countByShelterGroupByCareRegNo(effective);
    }

//    내 보호센터 중 등록번호로 동물 리스트업
    public Map<String, List<RescuedResponseDto>> getAnimalsGroupedByCareRegNo(Long currentShelterId) {
        // 1) 내 보호소의 careRegNo 리스트 가져오기
        List<HashMap<String,Object>> careGroups = rescuedQueryMapper.countByShelterGroupByCareRegNo(currentShelterId);

        Map<String, List<RescuedResponseDto>> result = new HashMap<>();
        for (HashMap<String,Object> g : careGroups) {
            String careRegNo = (String) g.get("careRegNo");

            // 2) careRegNo별 목록 조회
            RescuedRequestDto q = new RescuedRequestDto();
            q.setCareRegNo(careRegNo);

            List<RescuedResponseDto> animals = rescuedQueryMapper.findAnimalsByShelter(q);
            result.put(careRegNo, animals);
        }
        return result;
    }

//    이관 필요 동물 리스트업
    public List<RescuedResponseDto> getTransferCandidates(
            Long shelterId,
            boolean usePeriod,
            int dueWithinDays,
            boolean useSeverity,
            String condition,
            String sort,
            String order,
            int offset,
            int limit
    ) {
        int safeOffset = Math.max(0, offset);
        int safeLimit  = Math.min(Math.max(1, limit), 100);

        // ✅ 정렬 화이트리스트: null/허용 외 값이면 sort=null 처리(→ XML에서 기본 다중 정렬 사용)
        Set<String> allowedSorts = Set.of("overdue", "daysProtected", "happenDt", "rescueDate", "age", "weight");
        String safeSort = (sort != null && allowedSorts.contains(sort)) ? sort : null;

        // ✅ 방향 보정: asc 아니면 desc
        String safeOrder = ("asc".equalsIgnoreCase(order)) ? "asc" : "desc";

        return rescuedQueryMapper.findTransferCandidates(
                shelterId, usePeriod, dueWithinDays, useSeverity, condition, safeSort, safeOrder, safeOffset, safeLimit
        );
    }
}
