package com.example.DIVE2025.domain.transferRequest.dto;

import com.example.DIVE2025.domain.transferRequest.enums.RequestStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Double fromShelterLatitude;
    private Double fromShelterLongitude;
    private RequestStatus requestStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
