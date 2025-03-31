package com.example.tileshop.dto.attribute;

import com.example.tileshop.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributeRequestDTO {
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(min = 3, max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String name;

    private Boolean isRequired = false;

    private String defaultValue;

}
