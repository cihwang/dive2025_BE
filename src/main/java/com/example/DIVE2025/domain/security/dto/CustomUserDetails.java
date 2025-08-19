package com.example.DIVE2025.domain.security.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import shop.ninescent.mall.member.domain.User;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // 도메인 User 객체 반환 (커스텀 메서드)
    public User getUser() {
        return user;
    }

    // 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한을 반환 (권한 로직이 없으면 null 또는 빈 리스트)
        return null; // 필요시 GrantedAuthority 목록 반환
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // 비밀번호 반환
    }

    @Override
    public String getUsername() {
        return user.getUserId(); // 사용자 ID 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정이 만료되지 않았는지 여부
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정이 잠기지 않았는지 여부
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명이 만료되지 않았는지 여부
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정이 활성화되었는지 여부
        return true;
    }
}
