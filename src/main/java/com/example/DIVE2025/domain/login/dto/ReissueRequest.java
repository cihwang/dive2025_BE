package com.example.DIVE2025.domain.login.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReissueRequest {

    @NotBlank
    private String refreshToken;
}
