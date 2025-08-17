package com.example.DIVE2025.domain.transferRequest.dto;

import com.example.DIVE2025.domain.transferRequest.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequestResponseDto {
    private Long id;
    private Long rescuedId;
    private String fromShelterName;
    private String toShelterName;
    private RequestStatus requestStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
