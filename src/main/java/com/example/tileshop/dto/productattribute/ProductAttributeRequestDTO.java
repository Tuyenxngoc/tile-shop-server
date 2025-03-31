package com.example.tileshop.dto.productattribute;

import com.example.tileshop.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAttributeRequestDTO {
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String value;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Long attributeId;
}
