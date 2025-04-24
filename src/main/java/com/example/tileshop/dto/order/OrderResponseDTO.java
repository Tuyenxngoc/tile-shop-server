package com.example.tileshop.dto.order;

import com.example.tileshop.constant.DeliveryMethod;
import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.constant.PaymentMethod;
import com.example.tileshop.constant.PaymentStatus;
import com.example.tileshop.dto.orderitem.OrderItemResponseDTO;
import com.example.tileshop.dto.user.UserResponseDTO;
import com.example.tileshop.entity.Order;
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

    public OrderResponseDTO(Order order) {
        this.createdDate = order.getCreatedDate();
        this.lastModifiedDate = order.getLastModifiedDate();
        this.id = order.getId();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        this.deliveryMethod = order.getDeliveryMethod();
        this.shippingAddress = order.getShippingAddress();
        this.paymentMethod = order.getPaymentMethod();
        this.note = order.getNote();
        this.transactionId = order.getTransactionId();
        this.paymentStatus = order.getPaymentStatus();
        this.paymentTime = order.getPaymentTime();
        this.responseCode = order.getResponseCode();
        this.user = new UserResponseDTO(order.getUser());
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponseDTO::new)
                .toList();
    }

}
