package com.example.DIVE2025.domain.security.dto;

import com.example.DIVE2025.domain.shelter.entity.Shelter;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private Long shelterId;
    private String username;
    private String password;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private Collection<? extends GrantedAuthority> authorities;

    /** 🔹 DB 로그인용 풀 생성자 */
    public CustomUserDetails(Long shelterId, String username, String password,
                             boolean enabled, boolean accountNonExpired,
                             boolean credentialsNonExpired, boolean accountNonLocked) {
        this.shelterId = shelterId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.emptyList();
    }

    /** 🔹 JWT 토큰 복원용 (비밀번호 불필요) */
    public CustomUserDetails(String username, Long shelterId) {
        this.shelterId = shelterId;
        this.username = username;
        this.password = ""; // 토큰 인증에는 패스워드 불필요
        this.enabled = true;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.accountNonLocked = true;
        this.authorities = Collections.emptyList();
    }

    /** 🔹 Shelter 엔티티 → CustomUserDetails 변환 */
    public static CustomUserDetails fromShelterEntity(Shelter shelter) {
        return new CustomUserDetails(
                shelter.getId(),
                shelter.getUsername(),
                shelter.getPassword(),
                true,  // enabled
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true   // accountNonLocked
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
