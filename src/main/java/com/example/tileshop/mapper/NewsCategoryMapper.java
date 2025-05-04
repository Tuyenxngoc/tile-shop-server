package com.example.tileshop.mapper;

import com.example.tileshop.dto.newscategory.NewsCategoryResponseDTO;
import com.example.tileshop.entity.NewsCategory;

public class NewsCategoryMapper {
    public static NewsCategoryResponseDTO toDTO(NewsCategory newscategory) {
        if (newscategory == null) {
            return null;
        }

        NewsCategoryResponseDTO dto = new NewsCategoryResponseDTO();
        dto.setCreatedDate(newscategory.getCreatedDate());
        dto.setLastModifiedDate(newscategory.getLastModifiedDate());
        dto.setId(newscategory.getId());
        dto.setName(newscategory.getName());
        dto.setSlug(newscategory.getSlug());

        return dto;
    }
}