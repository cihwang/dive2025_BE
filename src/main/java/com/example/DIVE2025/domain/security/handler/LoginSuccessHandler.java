package com.example.DIVE2025.domain.security.handler;

import com.example.DIVE2025.domain.security.JwtTokenProvider;
import com.example.DIVE2025.domain.security.dto.CustomUserDetails;
import com.example.DIVE2025.domain.security.dto.UserLoginResponseDTO;
import com.example.DIVE2025.domain.security.util.JsonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        // 인증된 사용자 정보
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // JWT 토큰 생성 (username + shelterId 포함됨)
        String token = jwtTokenProvider.generateToken(authentication);

        // 응답 DTO 작성
        UserLoginResponseDTO result = UserLoginResponseDTO.builder()
                .token(token)
                .shelterId(userDetails.getShelterId())
                .username(userDetails.getUsername())
                .build();

        // JSON 응답 전송
        JsonResponse.send(response, result);
    }
}
