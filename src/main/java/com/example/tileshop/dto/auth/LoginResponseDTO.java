package com.example.tileshop.dto.auth;

import com.example.tileshop.constant.CommonConstant;
import lombok.Getter;

@Getter
public class LoginResponseDTO {

    private final String tokenType = CommonConstant.TOKEN_TYPE;

    private final String accessToken;

    private final String refreshToken;

    public LoginResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
