package com.example.DIVE2025.domain.security.config;

import com.example.DIVE2025.domain.security.CustomUserDetailsService;
import com.example.DIVE2025.domain.security.filter.JwtAuthenticationFilter;
import com.example.DIVE2025.domain.security.handler.CustomAccessDeniedHandler;
import com.example.DIVE2025.domain.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    // username으로 계정을 로드하는 너희 서비스 (DB에서 해시 비번 반환)
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ Swagger / OpenAPI 문서 경로 허용
                        .requestMatchers(
                                "/env",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/actuator/health"
                        ).permitAll()

                        // ✅ 로그인·OAuth 진입점은 공개
                        .requestMatchers("/api/auth/**", "/oauth2/**").permitAll()

                        // ✅ 관리자 전용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ✅ 사용자 API는 인증 필요(변경)
                        //   주의: Security의 .requestMatchers는 {username} 같은 변수를 해석하지 않음.
                        //   필요 시 그냥 "/api/user/**"로 두거나, MvcRequestMatcher를 사용하세요.
                        .requestMatchers("/api/user/delete-member").authenticated()
                        .requestMatchers("/api/user/**").authenticated()

                        // ✅ 그 외 전부 인증 필요(기존의 anyRequest().permitAll()은 보안상 위험)
                        .anyRequest().authenticated()
                )
                // 인증 프로바이더
                .authenticationProvider(authenticationProvider)
                // JWT 필터를 UsernamePasswordAuthenticationFilter 이전에 배치
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // ✅ DaoAuthenticationProvider: UserDetailsService + PasswordEncoder 연결
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService); // username으로만 조회
        provider.setPasswordEncoder(passwordEncoder);             // 내부에서 matches(raw, encoded) 호출
        // 필요시: provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    // ✅ BCrypt 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // 여러 포맷 혼용 시:
        // return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // AuthenticationManager는 위 Provider들을 포함해 구성됨
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // CORS (필요에 맞게 조정)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
