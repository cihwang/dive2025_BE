package com.example.DIVE2025.domain.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 로그 출력
        log.error("접근 거부: 요청 URL = {}, 에러 메시지 = {}", request.getRequestURI(), accessDeniedException.getMessage());

        // HTTP 상태 코드 설정
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 형태의 응답 메시지 작성
        String jsonResponse = String.format("{\"error\": \"권한이 부족합니다.\", \"status\": %d}", HttpStatus.FORBIDDEN.value());
        response.getWriter().write(jsonResponse);
    }
}
