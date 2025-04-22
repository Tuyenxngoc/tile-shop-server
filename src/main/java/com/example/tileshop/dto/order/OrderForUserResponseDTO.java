package com.example.tileshop.dto.order;

import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.constant.PaymentMethod;
import com.example.tileshop.constant.PaymentStatus;
import com.example.tileshop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderForUserResponseDTO {
    private long id;

    private double totalAmount;

    private OrderStatus status;

    private String deliveryMethod;

    private String shippingAddress;

    private PaymentMethod paymentMethod;

    private String note;

    private PaymentStatus paymentStatus;

    private LocalDateTime paymentTime;

    public OrderForUserResponseDTO(Order order) {
        this.id = order.getId();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        this.deliveryMethod = order.getDeliveryMethod();
        this.shippingAddress = order.getShippingAddress();
        this.paymentMethod = order.getPaymentMethod();
        this.note = order.getNote();
        this.paymentStatus = order.getPaymentStatus();
        this.paymentTime = order.getPaymentTime();
    }

}
