package com.example.tileshop.dto.order;

import com.example.tileshop.constant.DeliveryMethod;
import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.constant.PaymentMethod;
import com.example.tileshop.constant.PaymentStatus;
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

    private String shippingAddress;

    private PaymentMethod paymentMethod;

    private String note;

    private String transactionId;

    private PaymentStatus paymentStatus;

    private LocalDateTime paymentTime;

    private String responseCode;

    private UserResponseDTO user;

    private List<OrderItemResponseDTO> orderItems;
}
