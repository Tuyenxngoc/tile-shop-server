package com.example.tileshop.controller;

import com.example.tileshop.annotation.CurrentUser;
import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.cart.CartItemRequestDTO;
import com.example.tileshop.security.CustomUserDetails;
import com.example.tileshop.service.CartService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Cart")
public class CartController {

    CartService cartService;

    @Operation(summary = "API Get All Items in Cart")
    @GetMapping(UrlConstant.Cart.GET_ALL)
    public ResponseEntity<?> getCartItems(@CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(cartService.getCartItems(userDetails.getUserId()));
    }

    @Operation(summary = "API Add Item to Cart")
    @PostMapping(UrlConstant.Cart.ADD_ITEM)
    public ResponseEntity<?> addItemToCart(
            @RequestBody @Valid CartItemRequestDTO requestDTO,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, cartService.addItemToCart(requestDTO, userDetails.getUserId()));
    }

    @Operation(summary = "API Update Item Quantity in Cart")
    @PutMapping(UrlConstant.Cart.UPDATE_ITEM)
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long productId,
            @RequestParam("quantity") int quantity,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(cartService.updateCartItem(productId, quantity, userDetails.getUserId()));
    }

    @Operation(summary = "API Remove Item from Cart")
    @DeleteMapping(UrlConstant.Cart.REMOVE_ITEM)
    public ResponseEntity<?> removeItemFromCart(
            @PathVariable Long productId,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(cartService.removeItemFromCart(productId, userDetails.getUserId()));
    }

    @Operation(summary = "API Clear Cart")
    @DeleteMapping(UrlConstant.Cart.CLEAR)
    public ResponseEntity<?> clearCart(@CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(cartService.clearCart(userDetails.getUserId()));
    }
}
