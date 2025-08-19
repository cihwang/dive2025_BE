package com.example.DIVE2025.domain.security.filter;

import com.example.DIVE2025.domain.security.JwtTokenProvider;
import com.example.DIVE2025.domain.security.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String COOKIE_NAME = "ACCESS_TOKEN";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String token = resolveToken(request);

                if (token != null && jwtTokenProvider.validateToken(token)) {
                    // ✅ 토큰에서 username / shelterId 추출
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    Long shelterId = jwtTokenProvider.getShelterIdFromToken(token);

                    // ✅ CustomUserDetails 생성
                    CustomUserDetails userDetails = new CustomUserDetails(username, shelterId);

                    // ✅ Authentication 객체 생성
                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            log.warn("JWT authentication failed: {}", ex.getMessage(), ex);
            SecurityContextHolder.clearContext(); // 실패 시 컨텍스트 초기화
        }

        filterChain.doFilter(request, response);
    }

    /** Header 우선, 없으면 쿠키(ACCESS_TOKEN)에서 추출 */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(TOKEN_HEADER);
        if (bearer != null && bearer.startsWith(TOKEN_PREFIX)) {
            return bearer.substring(TOKEN_PREFIX.length());
        }

        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> COOKIE_NAME.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getServletPath();

        // ✅ Swagger / 문서 / 헬스체크는 JWT 필터 우회
        if (p.equals("/swagger-ui.html")
                || p.startsWith("/swagger-ui")
                || p.startsWith("/v3/api-docs")
                || p.startsWith("/actuator/health")) {
            return true;
        }

        // ✅ 공개 엔드포인트(로그인·OAuth 콜백 등)도 우회
        if (p.startsWith("/api/auth")
                || p.startsWith("/oauth2")) {
            return true;
        }

        // ✅ 정적 리소스 우회
        if (p.startsWith("/css/")
                || p.startsWith("/js/")
                || p.startsWith("/images/")) {
            return true;
        }

        // (선택) 에러 페이지 우회: 오류 처리 중 재귀 방지
        if (p.equals("/error")) {
            return true;
        }

        return false;
    }

}
