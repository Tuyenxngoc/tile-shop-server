package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDto;
import com.example.tileshop.dto.request.auth.*;
import com.example.tileshop.dto.response.auth.CurrentUserLoginResponseDto;
import com.example.tileshop.dto.response.auth.LoginResponseDto;
import com.example.tileshop.dto.response.auth.TokenRefreshResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto request);

    CommonResponseDto logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

    TokenRefreshResponseDto refresh(TokenRefreshRequestDto request);

    CommonResponseDto forgotPassword(ForgotPasswordRequestDto requestDto);

    CommonResponseDto changePassword(ChangePasswordRequestDto requestDto, String userId);

    CurrentUserLoginResponseDto getCurrentUser(String userId);

    CommonResponseDto register(RegisterRequestDto requestDto);

}
