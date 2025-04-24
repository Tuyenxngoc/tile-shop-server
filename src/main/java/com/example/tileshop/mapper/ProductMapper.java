package com.example.tileshop.mapper;

import com.example.tileshop.dto.product.ProductResponseDTO;
import com.example.tileshop.entity.Product;

public class ProductMapper {

    public static ProductResponseDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO dto = new ProductResponseDTO();
        // TODO: set fields từ Product vào dto
        // vd: dto.setId(product.getId());

        return dto;
    }

}