package com.example.tileshop.mapper;

import com.example.tileshop.dto.attribute.AttributeResponseDTO;
import com.example.tileshop.entity.Attribute;

public class AttributeMapper {
    public static AttributeResponseDTO toDTO(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        AttributeResponseDTO dto = new AttributeResponseDTO();
        // TODO: set fields từ Attribute vào dto
        // vd: dto.setId(attribute.getId());

        return dto;
    }
}