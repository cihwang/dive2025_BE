package com.example.DIVE2025.domain.transporterRequest.dto;

import com.example.DIVE2025.domain.transporterRequest.enums.TprDecisionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TprListResponseDto {
    private Long id;
    private Long transferRequestId;
    private Long transporterId;
    private String fromShelterName;
    private String toShelterName;
    private String message;
    private TprDecisionStatus tprDecisionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
