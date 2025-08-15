package com.example.DIVE2025.domain.login.cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieFactory {

    @Value("${app.cookie.same-site:Lax}")
    private String sameSite;

    @Value("${app.cookie.secure:false}")
    private boolean secure;

    @Value("${app.cookie.domain:}")
    private String domain;

    public ResponseCookie build(String name, String value, long maxAgeMs) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeMs))
                .sameSite(sameSite);
        if (domain != null && !domain.isBlank()) {
            cookieBuilder.domain(domain);
        }
        return cookieBuilder.build();
    }

    public ResponseCookie delete(String name) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSite);
        if (domain != null && !domain.isBlank()) {
            cookieBuilder.domain(domain);
        }
        return cookieBuilder.build();
    }
}
