package com.example.DIVE2025.domain.transferRequest.dto;

import com.example.DIVE2025.domain.transferRequest.enums.RequestDecision;
import com.example.DIVE2025.domain.transferRequest.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrUpdateRequestDto {
    private Long trRequestId;
    private RequestDecision requestDecision;
    private RequestStatus requestStatus;
    private String message;
}
