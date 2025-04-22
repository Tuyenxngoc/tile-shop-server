package com.example.tileshop.dto.order;

import com.example.tileshop.constant.PaymentMethod;
import com.example.tileshop.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponse {
    private String message;
    private Long orderId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Double totalAmount;
}
