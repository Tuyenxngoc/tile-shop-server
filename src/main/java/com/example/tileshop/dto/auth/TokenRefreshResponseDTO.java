package com.example.tileshop.dto.auth;

import com.example.tileshop.constant.CommonConstant;
import lombok.Getter;

@Getter
public class TokenRefreshResponseDTO {

    private final String tokenType = CommonConstant.TOKEN_TYPE;

    private final String accessToken;

    private final String refreshToken;

    public TokenRefreshResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
