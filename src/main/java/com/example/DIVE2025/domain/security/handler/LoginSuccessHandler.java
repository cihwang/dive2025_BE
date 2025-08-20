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
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(authentication);

        // stype/sid 계산
        String stype = principal.getStype(); // SHELTER | TRANSPORTER
        Long sid = "SHELTER".equals(stype) ? principal.getShelterId() : principal.getTransporterId();

        // 응답 DTO 작성
        UserLoginResponseDTO result = UserLoginResponseDTO.builder()
                .token(token)
                .username(principal.getUsername())
                .role(principal.getRole())
                .stype(stype)
                .sid(sid)
                .shelterId("SHELTER".equals(stype) ? sid : null)
                .transporterId("TRANSPORTER".equals(stype) ? sid : null)
                .latitude(principal.getLatitude())
                .longitude(principal.getLongitude())
                .build();

        // JSON 응답
        JsonResponse.send(response, result);
    }
}
