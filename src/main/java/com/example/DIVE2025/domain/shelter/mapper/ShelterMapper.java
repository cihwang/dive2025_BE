package com.example.DIVE2025.domain.shelter.mapper;

import com.example.DIVE2025.domain.rescued.enums.AnimalCondition;
import com.example.DIVE2025.domain.shelter.dto.GetUsernameResponseDto;
import com.example.DIVE2025.domain.shelter.dto.RecommendResponseDto;
import com.example.DIVE2025.domain.shelter.dto.GetUsernameRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelterMapper {

    /**
     * 매칭 우선순위: 1.shelter에 여분의 자리가 있는지 2. shelterFeature == animalCondition(일치하면 0. 불일치 1) 3. 거리 오름차순
     */
    List<RecommendResponseDto> recommendedShelterId(
            @Param("animalCondition")AnimalCondition animalCondition,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude);

    GetUsernameResponseDto getUsernameById(GetUsernameRequestDto getUsernameVO);
}
