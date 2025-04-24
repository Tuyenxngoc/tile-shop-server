package com.example.tileshop.dto.orderitem;

import com.example.tileshop.entity.OrderItem;
import com.example.tileshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long id;

    private int quantity;

    private double priceAtTimeOfOrder;

    private Product product;

    public OrderItemResponseDTO(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.quantity = orderItem.getQuantity();
        this.priceAtTimeOfOrder = orderItem.getPriceAtTimeOfOrder();
    }
}
