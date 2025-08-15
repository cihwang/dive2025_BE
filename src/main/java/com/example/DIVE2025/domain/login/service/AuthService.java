package com.example.DIVE2025.domain.login.service;

import com.example.DIVE2025.domain.login.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    void loginAndSetCookies(
            LoginRequest request,
            HttpServletResponse response
    );

    void reissueCookies(
            HttpServletRequest request,
            HttpServletResponse response
    );

    void deleteCookies(
            HttpServletRequest request,
            HttpServletResponse response
    );
}
