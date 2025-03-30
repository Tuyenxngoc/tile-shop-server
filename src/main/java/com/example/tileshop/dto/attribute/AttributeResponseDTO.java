package com.example.tileshop.dto.attribute;

import com.example.tileshop.entity.Attribute;
import lombok.Getter;

@Getter
public class AttributeResponseDTO {

    private long id;

    private String name;

    private Boolean isRequired;

    private String defaultValue;

    public AttributeResponseDTO(Attribute attribute) {
        this.id = attribute.getId();
        this.name = attribute.getName();
        this.isRequired = attribute.isRequired();
        this.defaultValue = attribute.getDefaultValue();
    }
}
