package com.example.tileshop.service;

import com.example.tileshop.dto.cart.CartItemRequestDTO;
import com.example.tileshop.dto.cartitem.CartItemResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;

import java.util.List;

public interface CartService {
    List<CartItemResponseDTO> getCartItems(Long customerId);

    CommonResponseDTO addItemToCart(CartItemRequestDTO requestDTO, Long customerId);

    CommonResponseDTO updateCartItem(Long productId, int quantity, Long customerId);

    CommonResponseDTO removeItemFromCart(Long productId, Long customerId);

    CommonResponseDTO clearCart(Long customerId);
}