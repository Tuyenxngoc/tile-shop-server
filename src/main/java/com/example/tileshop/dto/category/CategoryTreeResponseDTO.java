package com.example.tileshop.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CategoryTreeResponseDTO {
    private long id;
    private String name;
    private List<CategoryTreeResponseDTO> subCategories;
}
