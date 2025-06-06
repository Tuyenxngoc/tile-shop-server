package com.example.tileshop.dto.product;

import com.example.tileshop.dto.category.CategorySimpleDTO;
import com.example.tileshop.dto.common.DateAuditingDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO extends DateAuditingDTO {
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

    private CategorySimpleDTO category;
}
