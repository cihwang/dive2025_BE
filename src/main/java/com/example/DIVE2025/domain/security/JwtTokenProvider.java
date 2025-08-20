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

    /** ✅ 로그인 성공 후 JWT 생성: username + role + stype + sid 포함 */
    public String generateToken(Authentication authentication) {
        initKey();
        CustomUserDetails u = (CustomUserDetails) authentication.getPrincipal();

        String stype = (u.getStype() != null) ? u.getStype() : "SHELTER"; // "SHELTER"|"TRANSPORTER"
        Long sid = "SHELTER".equals(stype) ? u.getShelterId() : u.getTransporterId();

        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(u.getUsername())     // username
                .claim("role",  u.getRole())     // 예: ROLE_SHELTER / ROLE_TRANSPORTER
                .claim("stype", stype)           // SHELTER / TRANSPORTER
                .claim("sid",   sid)             // 주체 id (shelterId 또는 transporterId)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)                   // 네가 쓰던 스타일 유지
                .compact();
    }


    /** ✅ 토큰 검증 */
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

    /** ✅ Claims 공통 파서 */
    private Claims parseClaims(String token) {
        initKey();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** username(subject) 추출 */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /** (호환용) SHELTER일 때만 shelterId 반환 */
    public Long getShelterIdFromToken(String token) {
        Claims c = parseClaims(token);
        String stype = c.get("stype", String.class);
        Number n = c.get("sid", Number.class);
        if ("SHELTER".equalsIgnoreCase(stype) && n != null) return n.longValue();
        return null;
    }

    /** role 추출 (예: ROLE_SHELTER) */
    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /** stype 추출 (SHELTER | TRANSPORTER) */
    public String getStypeFromToken(String token) {
        return parseClaims(token).get("stype", String.class);
    }

    /** sid 추출 (shelterId 또는 transporterId) */
    public Long getSidFromToken(String token) {
        Number n = parseClaims(token).get("sid", Number.class);
        return (n == null) ? null : n.longValue();
    }
}
