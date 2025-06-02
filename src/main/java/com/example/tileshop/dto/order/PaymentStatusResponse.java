package com.example.tileshop.dto.order;

import com.example.tileshop.constant.PaymentMethod;
import com.example.tileshop.constant.PaymentStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponse {
    private String message;
    private Long orderId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Double totalAmount;
}
