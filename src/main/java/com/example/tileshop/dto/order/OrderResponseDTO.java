package com.example.tileshop.dto.order;

import com.example.tileshop.constant.*;
import com.example.tileshop.dto.orderitem.OrderItemResponseDTO;
import com.example.tileshop.dto.user.UserResponseDTO;
import com.example.tileshop.entity.common.DateAuditing;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO extends DateAuditing {
    private long id;

    private double totalAmount;

    private OrderStatus status;

    private DeliveryMethod deliveryMethod;

    private String recipientName;

    private Gender recipientGender;

    private String recipientEmail;

    private String recipientPhone;

    private String shippingAddress;

    private PaymentMethod paymentMethod;

    private String note;

    private String cancelReason;

    private String transactionId;

    private PaymentStatus paymentStatus;

    private LocalDateTime paymentTime;

    private String responseCode;

    private UserResponseDTO user;

    private List<OrderItemResponseDTO> orderItems;
}
