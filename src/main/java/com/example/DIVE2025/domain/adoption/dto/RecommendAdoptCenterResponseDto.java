package com.example.DIVE2025.domain.adoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendAdoptCenterResponseDto {
    private Long id; // transporter_id
    private String username;
    private String tel;
    private String addr;
    private Double distance;
}
