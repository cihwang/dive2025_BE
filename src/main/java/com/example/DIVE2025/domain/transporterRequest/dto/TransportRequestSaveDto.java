package com.example.DIVE2025.domain.transporterRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportRequestSaveDto {
    private Long transferRequestId;
    private Long transporterId;
    private Long fromShelterId;
    private Long toShelterId;
    private String message;
}
