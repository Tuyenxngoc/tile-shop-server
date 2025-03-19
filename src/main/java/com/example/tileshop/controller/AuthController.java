package com.example.tileshop.controller;

import com.example.tileshop.annotation.CurrentUser;
import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.base.VsResponseUtil;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.domain.dto.request.auth.*;
import com.example.tileshop.security.CustomUserDetails;
import com.example.tileshop.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Auth")
public class AuthController {

    AuthService authService;

    @Operation(summary = "API Login")
    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequestDto request) {
        return VsResponseUtil.success(authService.login(request));
    }

    @Operation(summary = "API Logout")
    @PostMapping(UrlConstant.Auth.LOGOUT)
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        return VsResponseUtil.success(authService.logout(request, response, authentication));
    }

    @Operation(summary = "API Refresh token")
    @PostMapping(UrlConstant.Auth.REFRESH_TOKEN)
    public ResponseEntity<?> refresh(@Valid @RequestBody TokenRefreshRequestDto tokenRefreshRequestDto) {
        return VsResponseUtil.success(authService.refresh(tokenRefreshRequestDto));
    }

    @Operation(summary = "API Register")
    @PostMapping(UrlConstant.Auth.REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        return VsResponseUtil.success(HttpStatus.CREATED, authService.register(requestDto));
    }

    @Operation(summary = "API forget password")
    @PostMapping(UrlConstant.Auth.FORGET_PASSWORD)
    public ResponseEntity<?> adminForgetPassword(@Valid @RequestBody ForgetPasswordRequestDto requestDto) {
        return VsResponseUtil.success(authService.forgetPassword(requestDto));
    }

    @Operation(summary = "API change password")
    @PatchMapping(UrlConstant.Auth.CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(authService.changePassword(requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API get current user login")
    @GetMapping(UrlConstant.Auth.GET_CURRENT_USER)
    public ResponseEntity<?> getCurrentUser(@CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(authService.getCurrentUser(userDetails.getUserId()));
    }

}
