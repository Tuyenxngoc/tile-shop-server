package com.example.tileshop.mapper;

import com.example.tileshop.dto.order.OrderResponseDTO;
import com.example.tileshop.dto.user.UserResponseDTO;
import com.example.tileshop.entity.Order;

public class OrderMapper {

    public static OrderResponseDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setCreatedDate(order.getCreatedDate());
        dto.setLastModifiedDate(order.getLastModifiedDate());
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setDeliveryMethod(order.getDeliveryMethod());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setNote(order.getNote());
        dto.setTransactionId(order.getTransactionId());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaymentTime(order.getPaymentTime());
        dto.setResponseCode(order.getResponseCode());
        dto.setUser(new UserResponseDTO(order.getUser()));
        dto.setOrderItems(order.getOrderItems().stream()
                .map(OrderItemMapper::toDTO)
                .toList());

        return dto;
    }

}