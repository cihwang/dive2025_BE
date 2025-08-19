package com.example.DIVE2025.domain.security;

import com.example.DIVE2025.domain.security.dto.UserLoginRequestDTO;
import com.example.DIVE2025.domain.security.dto.UserLoginResponseDTO;
import com.example.DIVE2025.domain.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SecurityController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 보호소 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO loginRequest) {
        log.info("로그인 요청 - 보호소 username: {}", loginRequest.getUsername());

        // 사용자 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // 인증 정보 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(authentication);

        // 인증된 사용자 정보 가져오기
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        // 응답 생성 (보호소 정보 + 토큰)
        UserLoginResponseDTO response = UserLoginResponseDTO.builder()
                .token(token)
                .shelterId(principal.getShelterId())
                .username(principal.getUsername())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        log.info("로그아웃 요청");
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("로그아웃 성공");
    }
}
