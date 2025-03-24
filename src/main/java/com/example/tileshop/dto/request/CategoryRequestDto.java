package com.example.tileshop.dto.request;

import com.example.tileshop.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryRequestDto {

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(min = 20, max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String name;

    private Long parentId;

    private List<Long> attributeIds;

}
