package com.example.DIVE2025.domain.transporterRequest.entity;

import com.example.DIVE2025.domain.transporterRequest.enums.TprDecisionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportRequest {
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
