package com.example.DIVE2025.domain.security.filter;

import com.example.DIVE2025.domain.security.handler.LoginFailureHandler;
import com.example.DIVE2025.domain.security.handler.LoginSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtUsernamePasswordAuthenticationFilter(
            AuthenticationManager authenticationManager,
            LoginSuccessHandler loginSuccessHandler,
            LoginFailureHandler loginFailureHandler) {

        super.setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/api/auth/login");                // 로그인 URL
        setAuthenticationSuccessHandler(loginSuccessHandler);     // 성공 핸들러
        setAuthenticationFailureHandler(loginFailureHandler);     // 실패 핸들러
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                throw new RuntimeException("지원하지 않는 로그인 메서드");
            }

            // Content-Type 관대하게 판별 (charset 등 포함 허용)
            String ct = Optional.ofNullable(request.getContentType()).orElse("").toLowerCase(Locale.ROOT);
            boolean isJson = ct.startsWith(MediaType.APPLICATION_JSON_VALUE);               // application/json; charset=UTF-8
            boolean isForm = ct.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE);   // application/x-www-form-urlencoded

            String username;
            String password;

            if (isJson) {
                try (var is = request.getInputStream()) {
                    var node = objectMapper.readTree(is); // objectMapper 주입 가정
                    username = Optional.ofNullable(node.get("username")).map(JsonNode::asText).orElse("");
                    password = Optional.ofNullable(node.get("password")).map(JsonNode::asText).orElse("");
                }
            } else if (isForm) {
                username = Optional.ofNullable(request.getParameter("username")).orElse("");
                password = Optional.ofNullable(request.getParameter("password")).orElse("");
            } else {
                // Content-Type 누락/이상: JSON 시도 후 실패 시 폼 파라미터
                try (var is = request.getInputStream()) {
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                    if (!body.isEmpty() && (body.startsWith("{") || body.startsWith("["))) {
                        var node = objectMapper.readTree(body);
                        username = Optional.ofNullable(node.get("username")).map(JsonNode::asText).orElse("");
                        password = Optional.ofNullable(node.get("password")).map(JsonNode::asText).orElse("");
                    } else {
                        username = Optional.ofNullable(request.getParameter("username")).orElse("");
                        password = Optional.ofNullable(request.getParameter("password")).orElse("");
                    }
                }
            }

            // 유니코드 정규화 + trim
            username = java.text.Normalizer.normalize(username.trim(), java.text.Normalizer.Form.NFC);

            // ✅ 한글 허용 정규식 (2~50자). 필요 없으면 이 블록 자체를 지워도 됩니다.
            if (!username.matches("^[\\p{L}\\p{N}._@\\-\\s]{2,50}$")) {
                throw new RuntimeException("아이디/비밀번호를 확인하세요"); // 형식 에러 문구는 완화
            }
            if (password.isBlank()) {
                throw new RuntimeException("아이디/비밀번호를 확인하세요");
            }

            // 🔑 raw 비밀번호로 토큰 생성 → Provider에서 PasswordEncoder.matches(raw, encoded) 수행
            var authRequest = new UsernamePasswordAuthenticationToken(username, password);
            return this.getAuthenticationManager().authenticate(authRequest);

        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 파싱 실패", e);
        }
    }

}
