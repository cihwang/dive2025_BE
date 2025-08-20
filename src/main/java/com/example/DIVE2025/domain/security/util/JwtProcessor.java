package com.example.DIVE2025.domain.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtProcessor {
//    //Token 유효시간 설정
//    static private final long TOKEN_VALID_MILISECOND = 1000L * 60 * 60*2; //2시간
//
//    private String secretKey = "충분히 긴 임의의(랜덤한) 비밀키 문자열 배정 ";
//    private Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//
//    //private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);  -- 운영시 사용
//    //JWT 생성
//    public String generateToken(String subject) {
//        return Jwts.builder()
//                .setSubject(subject)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(new Date().getTime() + TOKEN_VALID_MILISECOND))
//                .signWith(key)
//                .compact();
//    }
//
//    // JWT Subject(username) 추출 - 해석 불가인 경우 예외 발생
//    // 예외 ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException,
//    //      IllegalArgumentException
//    public String getUsername(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    // JWT 검증(유효 기간 검증) - 해석 불가인 경우 예외 발생
//    public boolean validateToken(String jwtToken) {
//        Jws<Claims> claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(jwtToken);
//        return true;
//    }

    // Token 유효시간 설정 (2시간)
    private static final long TOKEN_VALID_MILLISECONDS = 1000L * 60 * 60 * 2;

    private final Key key;

    // 생성자에서 Secret Key를 Base64 디코딩하여 Key로 변환
    public JwtProcessor(@Value("${spring.jwt.secret}") String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey); // Base64 디코딩
        this.key = Keys.hmacShaKeyFor(decodedKey); // HS384에 적합한 Key 생성
    }

    // JWT 생성 (HS384 사용)
    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALID_MILLISECONDS))
                .signWith(key, SignatureAlgorithm.HS384) // HS384 명시적으로 설정
                .compact();
    }

    // JWT에서 사용자 ID(username) 추출
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 검증
    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}