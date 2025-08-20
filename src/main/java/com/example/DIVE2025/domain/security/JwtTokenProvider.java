package com.example.DIVE2025.domain.security;

import com.example.DIVE2025.domain.security.dto.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}") // 충분히 긴 비밀키 문자열
    private String secretKey;

    @Value("${spring.jwt.expiration}") // 토큰 만료 시간 (밀리초)
    private long expiration;

    private Key key;

    private void initKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        }
    }

    /** ✅ Authentication에서 username + shelterId 추출해 JWT 생성 */
    public String generateToken(Authentication authentication) {
        initKey();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())   // username
                .claim("shelterId", userDetails.getShelterId()) // shelterId claim 추가
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /** JWT에서 shelterId 추출 */
    public Long getShelterIdFromToken(String token) {
        initKey();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("shelterId", Long.class);
    }

    /** JWT에서 username(subject) 추출 */
    public String getUsernameFromToken(String token) {
        initKey();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** 토큰 검증 */
    public boolean validateToken(String token) {
        try {
            initKey();
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }
}
