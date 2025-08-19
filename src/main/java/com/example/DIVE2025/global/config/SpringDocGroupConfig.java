package com.example.DIVE2025.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocGroupConfig {

    /** 인증/토큰 */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    /** 회원/보호소 가입 및 내 정보 */
    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("member")
                .pathsToMatch("/api/member/**")
                .build();
    }

    /** 구조동물 조회/카운트/이관 후보 */
    @Bean
    public GroupedOpenApi rescuedApi() {
        return GroupedOpenApi.builder()
                .group("rescued")
                .pathsToMatch("/api/rescued/**")
                .build();
    }

    /** 보호소 추천(우선순위) */
    @Bean
    public GroupedOpenApi shelterApi() {
        return GroupedOpenApi.builder()
                .group("shelter")
                .pathsToMatch("/api/shelter/**")
                .build();
    }

    /** 이관 신청/수정/삭제/조회 */
    @Bean
    public GroupedOpenApi transferRequestApi() {
        return GroupedOpenApi.builder()
                .group("transfer-request")
                .pathsToMatch("/api/tr-request/**")
                .build();
    }

    /** 관리자용: 데이터 적재/동기화/점검 */
    @Bean
    public GroupedOpenApi adminRescuedApi() {
        return GroupedOpenApi.builder()
                .group("admin-rescued")
                .pathsToMatch("/admin/rescued/**")
                .build();
    }
}
