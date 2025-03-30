package com.example.tileshop.mapper;

import com.example.tileshop.dto.brand.BrandRequestDTO;
import com.example.tileshop.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toBrand(BrandRequestDTO requestDTO);
}