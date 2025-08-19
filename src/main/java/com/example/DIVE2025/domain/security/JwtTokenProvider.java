package com.example.DIVE2025.domain.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}") // 충분히 긴 비밀키 문자열
    private String secretKey;

    @Value("${jwt.expiration}") // 토큰 만료 시간 (밀리초)
    private long expiration;

    private Key key;

    // secretKey를 기반으로 Key 객체를 초기화
    private void initKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        }
    }

    // JWT 토큰 생성
    public String generateToken(Authentication authentication) {
        initKey(); // 키 초기화
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // 토큰에 사용자 정보 저장
                .setIssuedAt(now) // 발급 시간
                .setExpiration(expiryDate) // 만료 시간
                .signWith(key) // 서명
                .compact();
    }

    // JWT 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        initKey(); // 키 초기화
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // 서명 키 설정
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // subject(username) 반환
    }

    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            initKey(); // 키 초기화
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true; // 유효한 토큰
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT 토큰 검증 실패: " + e.getMessage());
            return false; // 잘못된 토큰
        }
    }

    // 인증 객체 생성
    public Authentication getAuthentication(String username) {
        return new UsernamePasswordAuthenticationToken(username, null, null);
    }

    // 토큰에서 역할 정보 추출
    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }
}
