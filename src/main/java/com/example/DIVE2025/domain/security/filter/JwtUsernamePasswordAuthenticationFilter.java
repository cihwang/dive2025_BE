package com.example.DIVE2025.domain.security.filter;

import com.example.DIVE2025.domain.security.handler.LoginFailureHandler;
import com.example.DIVE2025.domain.security.handler.LoginSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import shop.ninescent.mall.security.dto.UserLoginRequestDTO;

import java.io.IOException;

@Slf4j
@Component
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;

    // 스프링 생성자 주입을 통해 전달
    public JwtUsernamePasswordAuthenticationFilter(
            AuthenticationManager authenticationManager,
            LoginSuccessHandler loginSuccessHandler,
            LoginFailureHandler loginFailureHandler ) {
        super(authenticationManager);
        this.objectMapper = new ObjectMapper();

        setFilterProcessesUrl("/api/auth/login");		          // POST 로그인 요청 url
        setAuthenticationSuccessHandler(loginSuccessHandler);	// 로그인 성공 핸들러 등록
        setAuthenticationFailureHandler(loginFailureHandler);  // 로그인 실패 핸들러 등록
    }

    // 로그인 요청 URL인 경우 로그인 작업 처리
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
//            throws AuthenticationException {
//
//        // 요청 BODY의 JSON에서 username, password  LoginDTO
//        UserLoginRequestDTO login = LoginDTO.of(request);
//
//        // 인증 토큰(UsernamePasswordAuthenticationToken) 구성
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
//
//        // AuthenticationManager에게 인증 요청
//        return getAuthenticationManager().authenticate(authenticationToken);
//    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            // 요청 본문 디버깅
            String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            log.debug("Request Body: {}", requestBody);

            // JSON 데이터를 DTO로 변환
            UserLoginRequestDTO loginRequest = objectMapper.readValue(requestBody, UserLoginRequestDTO.class);

            // 인증 토큰 구성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getPassword());

            // AuthenticationManager에 인증 요청
            return getAuthenticationManager().authenticate(authenticationToken);

        } catch (IOException e) {
            log.error("JSON 파싱 실패: {}", e.getMessage());
            throw new RuntimeException("올바르지 않은 로그인 요청 형식입니다.");
        }
    }



}
