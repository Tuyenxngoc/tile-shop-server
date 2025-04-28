package com.example.tileshop.dto.product;

import com.example.tileshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductForUserResponseDTO {
    private long id;

    private String name;

    private String slug;

    private double price;

    private double discountPercentage;

    private double salePrice;

    private int stockQuantity;

    private double averageRating;

    private String imageUrl;

    public ProductForUserResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.slug = product.getSlug();
        this.price = product.getPrice();
        this.discountPercentage = product.getDiscountPercentage();
        this.salePrice = product.calculateFinalPrice();
        this.stockQuantity = product.getStockQuantity();
        this.averageRating = product.getAverageRating();
        this.imageUrl = product.getImages().getFirst().getImageUrl();
    }
}
