package com.example.tileshop.mapper;

import com.example.tileshop.dto.order.OrderResponseDTO;
import com.example.tileshop.entity.Order;

public class OrderMapper {

    public static OrderResponseDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponseDTO dto = new OrderResponseDTO();
        // TODO: set fields từ Order vào dto
        // vd: dto.setId(order.getId());

        return dto;
    }

}