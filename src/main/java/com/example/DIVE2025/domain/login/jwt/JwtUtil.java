package com.example.DIVE2025.domain.login.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    // 설정값 주입
    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),            // secret key 바이트
                Jwts.SIG.HS256.key().build().getAlgorithm()         // 알고리즘
        );
    }

    public String createAccess(String email, String username, String role, long expMs) {
        return Jwts.builder()
                .claim("category", "access")
                .claim("email", email)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expMs))
                .signWith(secretKey)
                .compact();
    }

    public String createRefresh(String email, String username, String role, long expMs) {
        return Jwts.builder()
                .claim("category", "refresh")
                .claim("email", email)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expMs))
                .signWith(secretKey)
                .compact();
    }

    public void validate(String token, String expectedCategory) {
        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (expectedCategory != null) {
            String cat = claims.get("category", String.class);
            if (!expectedCategory.equals(cat)) {
                throw new IllegalStateException("Invalid token category!");
            }
        }
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseClaimsJws(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("role", String.class);
    }
}
