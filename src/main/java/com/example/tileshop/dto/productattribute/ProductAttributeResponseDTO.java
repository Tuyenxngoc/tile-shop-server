package com.example.tileshop.dto.productattribute;

import com.example.tileshop.entity.ProductAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductAttributeResponseDTO {
    private String name;

    private String value;

    public ProductAttributeResponseDTO(ProductAttribute productAttribute) {
        this.name = productAttribute.getAttribute().getName();
        this.value = productAttribute.getValue();
    }

}
