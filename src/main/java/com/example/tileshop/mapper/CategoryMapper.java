package com.example.tileshop.mapper;

import com.example.tileshop.dto.category.CategoryResponseDTO;
import com.example.tileshop.entity.Category;

public class CategoryMapper {
    public static CategoryResponseDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponseDTO dto = new CategoryResponseDTO();
        // TODO: set fields từ Category vào dto
        // vd: dto.setId(category.getId());

        return dto;
    }
}