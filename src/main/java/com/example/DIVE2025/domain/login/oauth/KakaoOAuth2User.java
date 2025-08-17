package com.example.DIVE2025.domain.login.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class KakaoOAuth2User implements OAuth2User {

    private final Map<String, Object> attributes;
    private final String email;
    private final String nickname;
    private final String role;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role);
    }

    @Override
    public String getName() {
        return email;
    }

    public String getEmail() {
        return email;
    }
    public String getNickname() {
        return nickname;
    }
    public String getRole() {
        return role;
    }
}
