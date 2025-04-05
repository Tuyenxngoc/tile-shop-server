package com.example.tileshop.dto.product;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.dto.productattribute.ProductAttributeRequestDTO;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductRequestDTO {
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(min = 3, max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String name;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    private String description;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Double price;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    @Min(value = 0, message = ErrorMessage.INVALID_MINIMUM_ONE)
    @Max(value = 100, message = ErrorMessage.INVALID_MAXIMUM_ONE_HUNDRED)
    private Double discountPercentage;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Integer stockQuantity;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Long categoryId;

    private Long brandId;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private List<@NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
            ProductAttributeRequestDTO> attributes;
}
