package com.example.tileshop.dto.productattribute;

import com.example.tileshop.entity.Attribute;
import com.example.tileshop.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAttributeRequestDTO {

    private String value;

    private Product productId;

    private Attribute attributeId;

}
