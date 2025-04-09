package com.example.tileshop.dto.product;

import com.example.tileshop.dto.brand.BrandResponseDTO;
import com.example.tileshop.dto.productattribute.ProductAttributeResponseDTO;
import com.example.tileshop.dto.review.ReviewResponseDTO;
import com.example.tileshop.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProductDetailResponseDTO {
    private Long id;

    private String name;

    private String slug;

    private String description;

    private double price;

    private Double discountPercentage;

    private double salePrice;

    private int stockQuantity;

    private double averageRating;

    private Category category;

    private BrandResponseDTO brand;

    private List<String> images;

    private List<ReviewResponseDTO> reviews;

    private List<ProductAttributeResponseDTO> attributes;

    public ProductDetailResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.slug = product.getSlug();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.discountPercentage = product.getDiscountPercentage();
        this.salePrice = product.getPrice() - (product.getPrice() * product.getDiscountPercentage() / 100);
        this.stockQuantity = product.getStockQuantity();
        this.averageRating = product.getAverageRating();

//        this.category = product.getCategory();
//
        if (product.getBrand() != null) {
            this.brand = new BrandResponseDTO(product.getBrand());
        }

        this.images = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();

        this.reviews = new ArrayList<>();
        for (Review review : product.getReviews()) {
            this.reviews.add(new ReviewResponseDTO(review));
        }

        this.attributes = new ArrayList<>();
        for (ProductAttribute attribute : product.getAttributes()) {
            this.attributes.add(new ProductAttributeResponseDTO(attribute));
        }
    }
}
