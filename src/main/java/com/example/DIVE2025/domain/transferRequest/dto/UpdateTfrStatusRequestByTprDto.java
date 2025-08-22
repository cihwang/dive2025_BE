package com.example.DIVE2025.domain.transferRequest.dto;

import com.example.DIVE2025.domain.transporterRequest.enums.TprDecisionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTfrStatusRequestByTprDto {
    private Long id;
    private Long transporterId;
    private String message;
    private TprDecisionStatus tprDecisionStatus;
}
