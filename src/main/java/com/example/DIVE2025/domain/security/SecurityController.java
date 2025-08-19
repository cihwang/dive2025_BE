package com.example.DIVE2025.domain.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shop.ninescent.mall.member.domain.User;
import shop.ninescent.mall.security.dto.UserLoginRequestDTO;
import shop.ninescent.mall.security.dto.UserLoginResponseDTO;

@Slf4j
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class SecurityController {


    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * 사용자 로그인
     *
     * @param loginRequest 사용자 ID와 비밀번호를 포함한 요청
     * @return JWT 토큰 및 사용자 정보를 반환
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO loginRequest) {
        log.info("로그인 요청 - 사용자 ID: {}", loginRequest.getUserId());

        // 사용자 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getPassword())
        );

        // 인증 정보 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(authentication);

        // 응답 생성 (사용자 정보 포함)
        UserLoginResponseDTO response = UserLoginResponseDTO.builder()
                .token(token)
                .userId(loginRequest.getUserId())
                .role(User.Role.valueOf(jwtTokenProvider.extractRole(token))) // JWT에서 역할 정보 추출
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 로그아웃
     *
     * @return 로그아웃 성공 메시지 반환
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        log.info("로그아웃 요청");

        // SecurityContext에서 인증 정보 삭제
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("로그아웃 성공");
    }


}
