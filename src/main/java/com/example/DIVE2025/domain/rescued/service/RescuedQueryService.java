package com.example.DIVE2025.domain.rescued.service;

import com.example.DIVE2025.domain.rescued.dto.RescuedRequestDto;
import com.example.DIVE2025.domain.rescued.dto.RescuedResponseDto;
import com.example.DIVE2025.domain.rescued.mapper.RescuedQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (!"happenDt".equals(q.getSort())) {
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
}
