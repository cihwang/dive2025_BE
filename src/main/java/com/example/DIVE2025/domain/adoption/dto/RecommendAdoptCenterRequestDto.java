package com.example.DIVE2025.domain.adoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendAdoptCenterRequestDto {
    private Double fromShelterLatitude;
    private Double fromShelterLongitude;
}
