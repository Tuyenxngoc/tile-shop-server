package com.example.tileshop.mapper;

import com.example.tileshop.dto.newscategory.NewsCategoryResponseDTO;
import com.example.tileshop.entity.NewsCategory;

public class NewsCategoryMapper {
    public static NewsCategoryResponseDTO toDTO(NewsCategory newscategory) {
        if (newscategory == null) {
            return null;
        }

        NewsCategoryResponseDTO dto = new NewsCategoryResponseDTO();
        // TODO: set fields từ NewsCategory vào dto
        // vd: dto.setId(newscategory.getId());

        return dto;
    }
}