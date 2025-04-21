package com.example.tileshop.dto.cartitem;

import com.example.tileshop.entity.CartItem;
import com.example.tileshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItemResponseDTO {

    private int quantity;

    private long productId;

    private String name;

    private double price;

    private double discountPercentage;

    private double salePrice;

    private String imageUrl;

    public CartItemResponseDTO(CartItem cartItem) {
        this.quantity = cartItem.getQuantity();
        Product product = cartItem.getProduct();
        this.productId = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.discountPercentage = product.getDiscountPercentage();
        this.salePrice = product.calculateFinalPrice();
        this.imageUrl = product.getImages().getFirst().getImageUrl();
    }

}
