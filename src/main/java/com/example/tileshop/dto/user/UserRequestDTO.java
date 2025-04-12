package com.example.tileshop.dto.user;

import com.example.tileshop.constant.CommonConstant;
import com.example.tileshop.constant.ErrorMessage;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Pattern(regexp = CommonConstant.REGEXP_USERNAME, message = ErrorMessage.INVALID_FORMAT_USERNAME)
    private String username;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Pattern(regexp = CommonConstant.REGEXP_PASSWORD, message = ErrorMessage.INVALID_FORMAT_PASSWORD)
    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String password;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Email(message = ErrorMessage.INVALID_FORMAT_EMAIL)
    @Size(min = 5, max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String email;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Long roleId;
}
