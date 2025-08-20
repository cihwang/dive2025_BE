package com.example.DIVE2025.domain.transporterRequest.dto;

import com.example.DIVE2025.domain.transporterRequest.enums.TprDecisionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTprRequestDto {
    private Long id; // Tpr_id
    private Long transferRequestId;
    private TprDecisionStatus decisionStatus; // ACCEPT, REJECT
}
