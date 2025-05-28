package com.example.tileshop.dto.productattribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeResponseDTO {
    private long id;

    private String name;

    private String value;
}
