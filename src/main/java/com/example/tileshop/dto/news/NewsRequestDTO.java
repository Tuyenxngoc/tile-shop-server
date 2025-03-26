package com.example.tileshop.dto.news;

import com.example.tileshop.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsRequestDTO {

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(min = 3, max = 500, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String title;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(min = 3, max = 1500, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String description;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    private String content;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Long categoryId;

}
