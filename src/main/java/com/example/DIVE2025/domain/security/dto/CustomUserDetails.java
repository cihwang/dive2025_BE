package com.example.DIVE2025.domain.security.dto;

import com.example.DIVE2025.domain.shelter.entity.Shelter;
import com.example.DIVE2025.domain.transporterRequest.entity.Transporter;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class CustomUserDetails implements UserDetails {

    private Long shelterId;
    private Long transporterId;

    private String username;
    private String password;

    private String role;
    private String stype;

    private Double latitude;
    private Double longitude;

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private Collection<? extends GrantedAuthority> authorities;

    private CustomUserDetails(){}

    /** Shelter → UserDetails (role 컬럼 사용, 없으면 기본 ROLE_SHELTER) */
    public static CustomUserDetails fromShelterEntity(Shelter s) {
        CustomUserDetails u = new CustomUserDetails();
        u.shelterId = s.getId();
        u.username = s.getUsername();
        u.password = s.getPassword();
        u.stype = "SHELTER";
        u.role = normalizeRole(
                (s.getRole() == null || s.getRole().isBlank()) ? "ROLE_SHELTER" : s.getRole()
        );
        u.enabled = true;
        u.accountNonExpired = true;
        u.credentialsNonExpired = true;
        u.accountNonLocked = true;
        u.authorities = List.of(new SimpleGrantedAuthority(u.role));
        u.latitude = s.getLatitude();
        u.longitude = s.getLongitude();
        return u;
    }

    /** Transporter → UserDetails (role 컬럼 사용, 없으면 기본 ROLE_TRANSPORTER) */
    public static CustomUserDetails fromTransporter(Transporter t) {
        CustomUserDetails u = new CustomUserDetails();
        u.transporterId = t.getId();
        u.username = t.getUsername();
        u.password = t.getPassword();
        u.stype = "TRANSPORTER";
        u.role = normalizeRole(
                (t.getRole() == null || t.getRole().isBlank()) ? "ROLE_TRANSPORTER" : t.getRole()
        );
        u.enabled = true;
        u.accountNonExpired = true;
        u.credentialsNonExpired = true;
        u.accountNonLocked = true;
        u.authorities = List.of(new SimpleGrantedAuthority(u.role));
        u.latitude = t.getLatitude();
        u.longitude = t.getLongitude();
        return u;
    }

    /** JWT 클레임 → UserDetails (DB조회 없이 복원) */
    public static CustomUserDetails fromTokenClaims(String username, Long sid, String stype, String role) {
        CustomUserDetails u = new CustomUserDetails();
        u.username = username;
        u.stype = Objects.requireNonNullElse(stype, "SHELTER");
        u.role = normalizeRole(Objects.requireNonNullElse(role, "ROLE_SHELTER"));
        if ("SHELTER".equalsIgnoreCase(u.stype)) u.shelterId = sid; else u.transporterId = sid;
        u.password = "";
        u.enabled = u.accountNonExpired = u.credentialsNonExpired = u.accountNonLocked = true;
        u.authorities = List.of(new SimpleGrantedAuthority(u.role));
        return u;
    }

    private static String normalizeRole(String raw) {
        String r = raw.trim().toUpperCase();
        return r.startsWith("ROLE_") ? r : ("ROLE_" + r);
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return accountNonExpired; }
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }
    @Override public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    @Override public boolean isEnabled() { return enabled; }
}
