package com.example.DIVE2025.domain.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 보호소 로그인 응답 DTO
 * - shelter 자체가 user 이므로 userId/email/role 제거
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponseDTO {

    // 공통
    private String token;
    private String username;
    private String role;    // ROLE_SHELTER | ROLE_TRANSPORTER
    private String stype;   // SHELTER | TRANSPORTER
    private Long sid;       // 공용 PK (SHELTER이면 shelterId, TRANSPORTER이면 transporterId)

    // 명시/하위호환
    private Long shelterId;      // stype == SHELTER 일 때만 세팅
    private Long transporterId;  // stype == TRANSPORTER 일 때만 세팅

    private Double latitude;
    private Double longitude;

}
