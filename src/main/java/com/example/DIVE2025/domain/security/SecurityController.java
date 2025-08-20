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



    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO loginRequest) {
        System.out.println(">>>>>>HIT api/auth/login");
        log.info("로그인 요청 - username: {}", loginRequest.getUsername());

        // 사용자 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // 인증 정보 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성 (role/stype/sid 포함)
        String token = jwtTokenProvider.generateToken(authentication);

        // 인증된 사용자 정보
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        log.info("principal role={}, stype={}, shelterId={}, transporterId={}",
                principal.getRole(), principal.getStype(), principal.getShelterId(), principal.getTransporterId());

        String stype = principal.getStype(); // "SHELTER" | "TRANSPORTER"
        Long sid = "SHELTER".equals(stype) ? principal.getShelterId() : principal.getTransporterId();
        log.info("computed stype={}, sid={}", stype, sid);

        UserLoginResponseDTO response = UserLoginResponseDTO.builder()
                .token(token)
                .username(principal.getUsername())
                .role(principal.getRole())
                .stype(stype)
                .sid(sid)
                .shelterId("SHELTER".equals(stype) ? sid : null)         // 보호소 로그인일 때
                .transporterId("TRANSPORTER".equals(stype) ? sid : null) // 운송업자 로그인일 때
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
