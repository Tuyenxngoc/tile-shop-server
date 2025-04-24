package com.example.tileshop.mapper;

import com.example.tileshop.dto.orderitem.OrderItemResponseDTO;
import com.example.tileshop.entity.OrderItem;

public class OrderItemMapper {

    public static OrderItemResponseDTO toDTO(OrderItem orderitem) {
        if (orderitem == null) {
            return null;
        }

        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        // TODO: set fields từ OrderItem vào dto
        // vd: dto.setId(orderitem.getId());

        return dto;
    }

}