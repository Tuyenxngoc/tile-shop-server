package com.example.tileshop.mapper;

import com.example.tileshop.dto.productattribute.ProductAttributeResponseDTO;
import com.example.tileshop.entity.ProductAttribute;

public class ProductAttributeMapper {
    public static ProductAttributeResponseDTO toDTO(ProductAttribute productAttribute) {
        if (productAttribute == null) {
            return null;
        }

        ProductAttributeResponseDTO dto = new ProductAttributeResponseDTO();
        dto.setId(productAttribute.getId());
        dto.setName(productAttribute.getAttribute().getName());
        dto.setValue(productAttribute.getValue());

        return dto;
    }
}