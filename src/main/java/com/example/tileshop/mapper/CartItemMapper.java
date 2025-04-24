package com.example.tileshop.mapper;

import com.example.tileshop.dto.cartitem.CartItemResponseDTO;
import com.example.tileshop.entity.CartItem;

public class CartItemMapper {

    public static CartItemResponseDTO toDTO(CartItem cartitem) {
        if (cartitem == null) {
            return null;
        }

        CartItemResponseDTO dto = new CartItemResponseDTO();
        // TODO: set fields từ CartItem vào dto
        // vd: dto.setId(cartitem.getId());

        return dto;
    }

}