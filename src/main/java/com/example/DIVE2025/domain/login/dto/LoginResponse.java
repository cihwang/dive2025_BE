package com.example.DIVE2025.domain.login.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginResponse {
    Long shelterId;
    String email;
    String username;
    String role;
    String accessToken;
    String refreshToken;
}
