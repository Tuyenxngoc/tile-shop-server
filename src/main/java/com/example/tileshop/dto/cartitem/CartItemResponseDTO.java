package com.example.tileshop.dto.cartitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    private long id;

    private int quantity;

    private long productId;

    private String name;

    private double price;

    private double discountPercentage;

    private double salePrice;

    private String imageUrl;
}
