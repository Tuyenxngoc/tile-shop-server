package com.example.tileshop.service;

import com.example.tileshop.dto.auth.*;
import com.example.tileshop.dto.common.CommonResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);

    CommonResponseDTO logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

    TokenRefreshResponseDTO refresh(TokenRefreshRequestDTO request);

    CommonResponseDTO forgotPassword(ForgotPasswordRequestDTO requestDTO);

    CommonResponseDTO changePassword(ChangePasswordRequestDTO requestDTO, String userId);

    CurrentUserLoginResponseDTO getCurrentUser(String userId);

    CommonResponseDTO register(RegisterRequestDTO requestDTO);
}
