package com.example.tileshop.dto.brand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponseDTO {
    private long id;

    private String name;

    private String description;

    private String logoUrl;
}
