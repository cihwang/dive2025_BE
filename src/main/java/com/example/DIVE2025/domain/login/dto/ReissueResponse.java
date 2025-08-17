package com.example.DIVE2025.domain.login.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReissueResponse {

    String accessToken;
    String refreshToken;
}
