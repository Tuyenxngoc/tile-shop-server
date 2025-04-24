package com.example.tileshop.dto.user;

import com.example.tileshop.config.TrimStringDeserializer;
import com.example.tileshop.constant.CommonConstant;
import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.Gender;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Email(message = ErrorMessage.INVALID_FORMAT_EMAIL)
    @Size(min = 5, max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String email;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Pattern(regexp = CommonConstant.REGEXP_PHONE_NUMBER, message = ErrorMessage.INVALID_FORMAT_PHONE)
    @Size(min = 10, max = 20, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String phoneNumber;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Pattern(regexp = CommonConstant.REGEXP_FULL_NAME, message = ErrorMessage.INVALID_FORMAT_NAME)
    @Size(min = 2, max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String fullName;

    @Size(min = 5, max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String address;

    private Gender gender;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Long roleId;
}
