package com.example.tileshop.dto.statistics;

import com.example.tileshop.dto.category.CategorySimpleDTO;
import com.example.tileshop.entity.Product;
import com.example.tileshop.mapper.CategoryMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingProductDTO {
    private long id;

    private String name;

    private String slug;

    private double price;

    private double discountPercentage;

    private double salePrice;

    private int stockQuantity;

    private double averageRating;

    private String imageUrl;

    private CategorySimpleDTO category;

    private long totalQuantity;   // tổng số lượng bán ra

    private double totalAmount;   // tổng tiền bán ra

    public TopSellingProductDTO(Product product, long totalQuantity, double totalAmount) {
        this.id = product.getId();
        this.name = product.getName();
        this.slug = product.getSlug();
        this.price = product.getPrice();
        this.discountPercentage = product.getDiscountPercentage();
        this.salePrice = product.calculateFinalPrice();
        this.stockQuantity = product.getStockQuantity();
        this.averageRating = product.getAverageRating();
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            this.imageUrl = product.getImages().getFirst().getImageUrl();
        }
        this.category = CategoryMapper.toSimpleDTO(product.getCategory());
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }
}
