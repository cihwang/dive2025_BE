package com.example.DIVE2025.domain.login.service;

import com.example.DIVE2025.domain.login.cookie.CookieFactory;
import com.example.DIVE2025.domain.login.dto.LoginRequest;
import com.example.DIVE2025.domain.login.entity.RefreshTokenEntity;
import com.example.DIVE2025.domain.login.entity.UserEntity;
import com.example.DIVE2025.domain.login.jwt.JwtUtil;
import com.example.DIVE2025.domain.login.mapper.RefreshMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshMapper refreshMapper;
    private final CookieFactory cookieFactory;

    @Value("${app.jwt.access-exp-minutes:30}")
    private long accessExpMinutes;

    @Value("${app.jwt.refresh-exp-days:14}")
    private long refreshExpDays;

    @Override
    @Transactional
    public void loginAndSetCookies(LoginRequest request, HttpServletResponse response) {

        UserEntity user = userService.upsertByEmail(
                request.getEmail(),
                request.getUsername()
        );

        long accessMs = TimeUnit.MINUTES.toMillis(accessExpMinutes);
        long refreshMs = TimeUnit.DAYS.toMillis(refreshExpDays);

        String access = jwtUtil.createAccess(
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                accessMs
        );

        String refresh = jwtUtil.createRefresh(
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                refreshMs
        );

        refreshMapper.deleteByEmail(user.getEmail());
        refreshMapper.insert(RefreshTokenEntity.builder()
                        .email(user.getEmail())
                        .refresh(refresh)
                        .expiration(LocalDateTime.now().plusDays(refreshExpDays))
                        .build()
        );

        ResponseCookie accessCookie = cookieFactory.build("ACCESS", access, accessMs);
        ResponseCookie refreshCookie = cookieFactory.build("REFRESH", refresh, refreshMs);

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    @Override
    @Transactional
    public void reissueCookies(HttpServletRequest request, HttpServletResponse response) {

        String refresh = extractCookie(request, "REFRESH");
        if (refresh == null || refresh.isBlank()) {
            throw new IllegalArgumentException("Refresh token is null");
        }

        jwtUtil.validate(refresh, "refresh");
        if (!refreshMapper.existsByRefresh(refresh)) {
            throw new IllegalArgumentException("Refresh token not found");
        }

        String email    = jwtUtil.getEmail(refresh);
        String username = jwtUtil.getUsername(refresh);
        String role     = jwtUtil.getRole(refresh);

        long accessMs = TimeUnit.MINUTES.toMillis(accessExpMinutes);
        long refreshMs = TimeUnit.DAYS.toMillis(refreshExpDays);
        String newAccess = jwtUtil.createAccess(email, username, role, accessMs);
        String newRefresh = jwtUtil.createRefresh(email, username, role, refreshMs);

        refreshMapper.deleteByEmail(email);
        refreshMapper.insert(RefreshTokenEntity.builder()
                        .email(email)
                        .refresh(newRefresh)
                        .expiration(LocalDateTime.now().plusDays(refreshExpDays))
                        .build());

        ResponseCookie accessCookie = cookieFactory.build("ACCESS", newAccess, accessMs);
        ResponseCookie refreshCookie = cookieFactory.build("REFRESH", newRefresh, refreshMs);
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    @Override
    @Transactional
    public void deleteCookies(HttpServletRequest request, HttpServletResponse response) {

        String refresh = extractCookie(request, "REFRESH");
        if (refresh != null || !refresh.isBlank()) {
            try {
                String email = jwtUtil.getEmail(refresh);
                refreshMapper.deleteByEmail(email);
            } catch (Exception ignored) {
                // 아모고토 안함
            }
        }

        response.addHeader("Set-Cookie", cookieFactory.delete("ACCESS").toString());
        response.addHeader("Set-Cookie", cookieFactory.delete("REFRESH").toString());
    }

    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
