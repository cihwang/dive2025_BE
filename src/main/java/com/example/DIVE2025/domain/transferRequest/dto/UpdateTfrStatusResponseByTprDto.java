package com.example.DIVE2025.domain.transferRequest.dto;

import com.example.DIVE2025.domain.transferRequest.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTfrStatusResponseByTprDto {
    private Long id;
    private RequestStatus requestStatus;
}
