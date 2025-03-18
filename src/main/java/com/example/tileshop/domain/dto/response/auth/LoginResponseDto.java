package com.example.tileshop.domain.dto.response.auth;

import com.example.tileshop.constant.CommonConstant;
import lombok.Getter;

@Getter
public class LoginResponseDto {

    private final String tokenType = CommonConstant.TOKEN_TYPE;

    private final String accessToken;

    private final String refreshToken;

    public LoginResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
