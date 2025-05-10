package com.example.tileshop.mapper;

import com.example.tileshop.dto.category.CategoryResponseDTO;
import com.example.tileshop.dto.category.CategorySimpleDTO;
import com.example.tileshop.dto.category.CategoryTreeResponseDTO;
import com.example.tileshop.dto.common.BaseEntityDTO;
import com.example.tileshop.entity.Attribute;
import com.example.tileshop.entity.Category;
import com.example.tileshop.entity.CategoryAttribute;

public class CategoryMapper {
    public static CategoryResponseDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setCreatedDate(category.getCreatedDate());
        dto.setLastModifiedDate(category.getLastModifiedDate());
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setParent(category.getParent() != null
                ? new BaseEntityDTO(category.getParent().getId(), category.getParent().getName())
                : null);
        dto.setAttributeIds(category.getCategoryAttributes().stream()
                .map(CategoryAttribute::getAttribute)
                .map(Attribute::getId)
                .toList());

        return dto;
    }

    public static CategoryTreeResponseDTO toCategoryTreeDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryTreeResponseDTO dto = new CategoryTreeResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());

        return dto;
    }

    public static CategorySimpleDTO toSimpleDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategorySimpleDTO dto = new CategorySimpleDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());

        return dto;
    }
}