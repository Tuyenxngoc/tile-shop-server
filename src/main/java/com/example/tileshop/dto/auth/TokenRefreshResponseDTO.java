package com.example.tileshop.dto.auth;

import com.example.tileshop.constant.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponseDTO {
    private String tokenType = CommonConstant.TOKEN_TYPE;

    private String accessToken;

    private String refreshToken;

    public TokenRefreshResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
