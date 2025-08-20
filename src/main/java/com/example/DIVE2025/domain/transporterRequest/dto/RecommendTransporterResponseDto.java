package com.example.DIVE2025.domain.transporterRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendTransporterResponseDto {
    private Long id; // transporter_id
    private String storeName;
    private String tel;
    private String addr;
    private Double distance;
}
