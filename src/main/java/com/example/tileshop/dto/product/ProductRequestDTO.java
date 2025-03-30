package com.example.tileshop.dto.product;

import com.example.tileshop.dto.productattribute.ProductAttributeRequestDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductRequestDTO {
    private String name;

    private String description;

    private Double price;

    private Double discountPercentage;

    private Integer stockQuantity;

    private Double averageRating;

    private Long categoryId;

    private Long brandId;

    private List<ProductAttributeRequestDTO> attributes = new ArrayList<>();
}
