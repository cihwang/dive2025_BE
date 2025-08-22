package com.example.DIVE2025.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class WebConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ 프론트엔드 도메인 허용 (개발 + 운영)
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",   // 로컬 개발 (Vite 기본 포트)
                "https://paw-on.store"    // 배포 환경
        ));

        // ✅ 쿠키/세션(credentials) 허용
        config.setAllowCredentials(true);

        // ✅ 허용할 메서드
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // ✅ 허용할 헤더
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Cache-Control",
                "Content-Type"
        ));

        // ✅ 적용할 URL 패턴 (/api/**, /admin/** 둘 다 포함)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
