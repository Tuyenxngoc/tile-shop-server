package com.example.tileshop.dto.productattribute;

import com.example.tileshop.entity.ProductAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeResponseDTO {
    private String name;

    private String value;

    public ProductAttributeResponseDTO(ProductAttribute productAttribute) {
        this.name = productAttribute.getAttribute().getName();
        this.value = productAttribute.getValue();
    }
}
