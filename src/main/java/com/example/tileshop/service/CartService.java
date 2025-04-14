package com.example.tileshop.service;

import com.example.tileshop.dto.cart.CartItemRequestDTO;
import com.example.tileshop.dto.cartitem.CartItemResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;

import java.util.List;

public interface CartService {
    List<CartItemResponseDTO> getCartItems(String userId);

    CommonResponseDTO addItemToCart(CartItemRequestDTO requestDTO, String userId);

    CommonResponseDTO updateCartItem(Long productId, int quantity, String userId);

    CommonResponseDTO removeItemFromCart(Long productId, String userId);

    CommonResponseDTO clearCart(String userId);
}