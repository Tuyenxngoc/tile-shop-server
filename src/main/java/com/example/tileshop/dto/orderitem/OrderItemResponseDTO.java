package com.example.tileshop.dto.orderitem;

import com.example.tileshop.dto.product.ProductResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private long id;

    private int quantity;

    private double priceAtTimeOfOrder;

    private ProductResponseDTO product;
}
