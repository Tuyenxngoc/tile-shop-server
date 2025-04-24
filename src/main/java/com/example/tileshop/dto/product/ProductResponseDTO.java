package com.example.tileshop.dto.product;

import com.example.tileshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private long id;

    private String name;

    private String slug;

    private String description;

    private double price;

    private double discountPercentage;

    private double salePrice;

    private int stockQuantity;

    private double averageRating;

    private String imageUrl;

    public ProductResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.slug = product.getSlug();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.discountPercentage = product.getDiscountPercentage();
        this.salePrice = product.calculateFinalPrice();
        this.stockQuantity = product.getStockQuantity();
        this.averageRating = product.getAverageRating();
        this.imageUrl = product.getImages().getFirst().getImageUrl();
    }

}
