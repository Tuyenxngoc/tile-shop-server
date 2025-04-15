package com.example.tileshop.dto.brand;

import com.example.tileshop.config.TrimStringDeserializer;
import com.example.tileshop.constant.ErrorMessage;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandRequestDTO {
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(min = 3, max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String name;

    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String description;
}
