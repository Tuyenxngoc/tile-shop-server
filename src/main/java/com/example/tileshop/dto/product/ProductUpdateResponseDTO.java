package com.example.tileshop.dto.product;

import com.example.tileshop.dto.productattribute.ProductAttributeRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateResponseDTO {
    private String name;

    private String slug;

    private String description;

    private double price;

    private int discountPercentage;

    private int stockQuantity;

    private Long categoryId;

    private Long brandId;

    private List<ProductAttributeRequestDTO> attributes;

    private List<String> images;
}
