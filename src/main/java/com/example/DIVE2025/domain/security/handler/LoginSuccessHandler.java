package com.example.DIVE2025.domain.security.handler;

import com.example.DIVE2025.domain.security.util.JsonResponse;
import com.example.DIVE2025.domain.security.util.JwtProcessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import shop.ninescent.mall.member.domain.User;
import shop.ninescent.mall.security.dto.CustomUserDetails;
import shop.ninescent.mall.security.dto.UserLoginResponseDTO;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProcessor jwtProcessor;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // SecurityContext에 저장된 인증 객체에서 Principal(사용자 정보) 추출
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        //CustomUserDetails에서 User 객체 추출
        User user = customUserDetails.getUser();

        // 로그인 성공 결과를 JSON 응답으로 전송
        UserLoginResponseDTO result = makeUserLoginResponse(user);
        JsonResponse.send(response, result);
    }

    private UserLoginResponseDTO makeUserLoginResponse(User user) {
        // JWT 토큰 생성
        String token = jwtProcessor.generateToken(user.getUserId());

        // 사용자 정보를 DTO에 매핑하여 반환
        return UserLoginResponseDTO.builder()
                .token(token)
                .userNo(user.getUserNo())
                .name(user.getName())
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(User.Role.valueOf(user.getRole().name())) // Enum 타입을 문자열로 변환
                .build();
    }
}
