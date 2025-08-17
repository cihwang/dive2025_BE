package com.example.DIVE2025.domain.shelter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendResponseDto {

    private Long id;
    private String username;
    private String description;
    private Double distance;
    private Integer match_priority;
}
