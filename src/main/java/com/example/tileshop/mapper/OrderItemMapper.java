package com.example.tileshop.mapper;

import com.example.tileshop.dto.orderitem.OrderItemResponseDTO;
import com.example.tileshop.entity.OrderItem;

public class OrderItemMapper {
    public static OrderItemResponseDTO toDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setId(orderItem.getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPriceAtTimeOfOrder(orderItem.getPriceAtTimeOfOrder());
        dto.setProduct(ProductMapper.toResponseDTO(orderItem.getProduct()));

        return dto;
    }
}