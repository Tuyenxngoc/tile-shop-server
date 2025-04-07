package com.example.tileshop.dto.brand;

import com.example.tileshop.entity.Brand;
import lombok.Getter;

@Getter
public class BrandForUserResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String logoUrl;

    public BrandForUserResponseDTO(Brand brand) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.description = brand.getDescription();
        this.logoUrl = brand.getLogoUrl();
    }

}
