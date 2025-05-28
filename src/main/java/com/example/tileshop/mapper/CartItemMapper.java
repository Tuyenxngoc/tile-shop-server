package com.example.tileshop.mapper;

import com.example.tileshop.dto.cartitem.CartItemResponseDTO;
import com.example.tileshop.entity.CartItem;
import com.example.tileshop.entity.Product;

public class CartItemMapper {
    public static CartItemResponseDTO toDTO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(cartItem.getId());
        dto.setQuantity(cartItem.getQuantity());

        Product product = cartItem.getProduct();
        dto.setProductId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setSalePrice(product.calculateFinalPrice());
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            dto.setImageUrl(product.getImages().getFirst().getImageUrl());
        }

        return dto;
    }
}