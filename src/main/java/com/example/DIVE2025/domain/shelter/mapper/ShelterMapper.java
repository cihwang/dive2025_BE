package com.example.DIVE2025.domain.shelter.mapper;

import com.example.DIVE2025.domain.rescued.enums.AnimalCondition;
import com.example.DIVE2025.domain.shelter.dto.RecommendRequestDto;
import com.example.DIVE2025.domain.shelter.dto.RecommendResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelterMapper {

    /**
     * 매칭 우선순위: 1. shelterFeature == animalCondition(일치하면 0. 불일치 1) 2. 거리 오름차순
     */
    List<RecommendResponseDto> recommendedShelterId(
            @Param("animalCondition")AnimalCondition animalCondition,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude);
}
