package com.example.tileshop.dto.order;

import com.example.tileshop.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentResponseDTO {
    private Long orderId;
    private double amount;

    public OrderPaymentResponseDTO(Order order) {
        this.orderId = order.getId();
        this.amount = order.getTotalAmount();
    }

}
