package com.example.DIVE2025.domain.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;


@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // HTTP 응답 상태를 401로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 응답 메시지를 작성 (JSON 형식 또는 간단한 텍스트)
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"인증이 필요합니다.\"}");
    }

}
