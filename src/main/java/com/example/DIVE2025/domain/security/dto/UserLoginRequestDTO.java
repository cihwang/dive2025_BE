package com.example.DIVE2025.domain.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 보호소 로그인 요청 DTO
 * - shelter.username (로그인 ID)
 * - shelter.password (비밀번호)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequestDTO {

    @JsonProperty("username")
    private String username; // 보호소 로그인 ID

    @JsonProperty("password")
    private String password; // 비밀번호
}
