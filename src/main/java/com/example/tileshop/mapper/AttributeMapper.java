package com.example.tileshop.mapper;

import com.example.tileshop.dto.attribute.AttributeResponseDTO;
import com.example.tileshop.entity.Attribute;

public class AttributeMapper {
    public static AttributeResponseDTO toDTO(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        AttributeResponseDTO dto = new AttributeResponseDTO();
        dto.setCreatedDate(attribute.getCreatedDate());
        dto.setLastModifiedDate(attribute.getLastModifiedDate());
        dto.setId(attribute.getId());
        dto.setName(attribute.getName());
        dto.setIsRequired(attribute.isRequired());
        dto.setDefaultValue(attribute.getDefaultValue());

        return dto;
    }
}