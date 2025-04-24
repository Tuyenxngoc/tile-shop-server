package com.example.tileshop.dto.brand;

import com.example.tileshop.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String logoUrl;

    public BrandResponseDTO(Brand brand) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.description = brand.getDescription();
        this.logoUrl = brand.getLogoUrl();
    }

}
