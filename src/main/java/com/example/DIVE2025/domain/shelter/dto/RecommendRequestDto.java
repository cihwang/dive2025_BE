package com.example.DIVE2025.domain.shelter.dto;

import com.example.DIVE2025.domain.rescued.enums.AnimalCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendRequestDto {
    private AnimalCondition animalCondition;
    private Double longitude;
    private Double latitude;
}
