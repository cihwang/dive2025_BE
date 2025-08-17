package com.example.DIVE2025.domain.shelter.service;

import com.example.DIVE2025.domain.shelter.dto.RecommendRequestDto;
import com.example.DIVE2025.domain.shelter.dto.RecommendResponseDto;
import com.example.DIVE2025.domain.shelter.mapper.ShelterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShelterService {

    private final ShelterMapper shelterMapper;

    @Autowired
    public ShelterService(ShelterMapper shelterMapper) {
        this.shelterMapper = shelterMapper;
    }

    public List<RecommendResponseDto> findShelterPriority(RecommendRequestDto recommendRequestDto) {
        return shelterMapper.recommendedShelterId(recommendRequestDto.getAnimalCondition(), recommendRequestDto.getLatitude(), recommendRequestDto.getLongitude());
    }
}
