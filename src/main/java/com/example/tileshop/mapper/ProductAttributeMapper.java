package com.example.tileshop.mapper;

import com.example.tileshop.dto.productattribute.ProductAttributeResponseDTO;
import com.example.tileshop.entity.ProductAttribute;

public class ProductAttributeMapper {

    public static ProductAttributeResponseDTO toDTO(ProductAttribute productattribute) {
        if (productattribute == null) {
            return null;
        }

        ProductAttributeResponseDTO dto = new ProductAttributeResponseDTO();
        // TODO: set fields từ ProductAttribute vào dto
        // vd: dto.setId(productattribute.getId());

        return dto;
    }

}