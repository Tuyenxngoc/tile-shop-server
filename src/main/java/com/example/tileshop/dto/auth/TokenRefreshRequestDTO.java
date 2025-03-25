package com.example.tileshop.dto.auth;

import com.example.tileshop.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequestDTO {

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    private String refreshToken;

}