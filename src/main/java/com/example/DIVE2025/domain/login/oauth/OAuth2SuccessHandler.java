package com.example.DIVE2025.domain.login.oauth;

import com.example.DIVE2025.domain.login.cookie.CookieFactory;
import com.example.DIVE2025.domain.login.entity.RefreshTokenEntity;
import com.example.DIVE2025.domain.login.jwt.JwtUtil;
import com.example.DIVE2025.domain.login.mapper.RefreshMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshMapper refreshMapper;
    private final CookieFactory cookieFactory;

    @Value("${app.jwt.access-exp-minutes:30}")
    private long accessExpMinutes;

    @Value("${app.jwt.refresh-exp-days:14}")
    private long refreshExpDays;

    @Value("${app.oauth2.redirect.front:http://localhost:5173/redirect}")
    private String frontRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        KakaoOAuth2User principal = (KakaoOAuth2User) authentication.getPrincipal();

        long accessMs = TimeUnit.MINUTES.toMillis(accessExpMinutes);
        long refreshMs = TimeUnit.DAYS.toMillis(refreshExpDays);

        String access = jwtUtil.createAccess(principal.getEmail(), principal.getNickname(), principal.getRole(), accessMs);
        String refresh = jwtUtil.createRefresh(principal.getEmail(), principal.getNickname(), principal.getRole(), refreshMs);

        refreshMapper.deleteByEmail(principal.getEmail());
        refreshMapper.insert(RefreshTokenEntity.builder()
                        .email(principal.getEmail())
                        .refresh(refresh)
                        .expiration(LocalDateTime.now().plusDays(refreshExpDays))
                        .build());

        ResponseCookie accessCookie = cookieFactory.build("ACCESS", access, accessMs);
        ResponseCookie refreshCookie = cookieFactory.build("REFRESH", refresh, refreshMs);

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        getRedirectStrategy().sendRedirect(request, response, frontRedirectUrl);
    }
}
