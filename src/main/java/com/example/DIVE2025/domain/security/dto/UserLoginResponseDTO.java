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

    private String token;       // JWT 액세스 토큰
    private Long shelterId;     // 보호소 고유 번호 (PK)
    private String username;    // 로그인 ID
    private String description; // 보호소 이름/설명

    // 필요시 추가
    private String tel;         // 보호소 전화번호
    private String addr;        // 보호소 주소
}
