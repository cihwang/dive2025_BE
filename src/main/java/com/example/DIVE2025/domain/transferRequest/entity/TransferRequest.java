package com.example.DIVE2025.domain.transferRequest.entity;

import com.example.DIVE2025.domain.transferRequest.enums.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {
    private Long id;
    private Long rescuedId;
    private Long fromShelterId;
    private Long toShelterId;
    private RequestStatus requestStatus;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
