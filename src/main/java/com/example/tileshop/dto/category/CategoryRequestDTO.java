package com.example.tileshop.dto.category;

import com.example.tileshop.config.TrimStringDeserializer;
import com.example.tileshop.constant.ErrorMessage;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(min = 3, max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String name;

    private Long parentId;

    private Set<@NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED) Long> attributeIds;
}
