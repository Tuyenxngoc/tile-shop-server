package com.example.tileshop.domain.dto.response.auth;

import com.example.tileshop.constant.CommonConstant;
import lombok.Getter;

@Getter
public class TokenRefreshResponseDto {

    private final String tokenType = CommonConstant.TOKEN_TYPE;

    private final String accessToken;

    private final String refreshToken;

    public TokenRefreshResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
