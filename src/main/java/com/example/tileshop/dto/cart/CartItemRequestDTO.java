package com.example.tileshop.dto.cart;

import com.example.tileshop.constant.ErrorMessage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDTO {
    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Long productId;

    @Min(value = 1, message = ErrorMessage.INVALID_MINIMUM_ONE)
    private int quantity;
}
