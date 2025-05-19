package com.example.tileshop.dto.statistics;

import com.example.tileshop.dto.category.CategorySimpleDTO;
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

    public TopSellingProductDTO(long id, String name, String slug, double price, double discountPercentage,
                                int stockQuantity, double averageRating, String imageUrl, CategorySimpleDTO category,
                                long totalQuantity, double totalAmount) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.price = price;
        this.discountPercentage = discountPercentage;
        if (discountPercentage > 0) {
            double discountAmount = (price * discountPercentage) / 100;
            this.salePrice = price - discountAmount;
        } else {
            this.salePrice = price;
        }
        this.stockQuantity = stockQuantity;
        this.averageRating = averageRating;
        this.imageUrl = imageUrl;
        this.category = category;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }
}
