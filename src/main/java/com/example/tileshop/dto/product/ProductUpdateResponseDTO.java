package com.example.tileshop.dto.product;

import com.example.tileshop.dto.productattribute.ProductAttributeRequestDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductUpdateResponseDTO {
    private String name;

    private String description;

    private Double price;

    private Double discountPercentage;

    private Integer stockQuantity;

    private Long categoryId;

    private Long brandId;

    private List<ProductAttributeRequestDTO> attributes;

    private List<String> images;
}
