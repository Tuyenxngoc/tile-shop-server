package com.example.tileshop.mapper;

import com.example.tileshop.dto.brand.BrandRequestDTO;
import com.example.tileshop.dto.brand.BrandResponseDTO;
import com.example.tileshop.entity.Brand;

public class BrandMapper {
    public static BrandResponseDTO toDTO(Brand brand) {
        if (brand == null) {
            return null;
        }

        BrandResponseDTO dto = new BrandResponseDTO();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setDescription(brand.getDescription());
        dto.setLogoUrl(brand.getLogoUrl());

        return dto;
    }

    public static Brand toEntity(BrandRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Brand brand = new Brand();
        brand.setName(dto.getName());
        brand.setDescription(dto.getDescription());

        return brand;
    }
}