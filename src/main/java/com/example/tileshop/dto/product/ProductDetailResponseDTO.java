package com.example.tileshop.dto.product;

import com.example.tileshop.dto.brand.BrandResponseDTO;
import com.example.tileshop.dto.category.CategorySimpleDTO;
import com.example.tileshop.dto.productattribute.ProductAttributeResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponseDTO {
    private long id;

    private String name;

    private String slug;

    private String description;

    private double price;

    private int discountPercentage;

    private double salePrice;

    private int stockQuantity;

    private double averageRating;

    private List<CategorySimpleDTO> categoryPath;

    private BrandResponseDTO brand;

    private List<String> images;

    private List<ProductAttributeResponseDTO> attributes;
}
