package com.example.tileshop.dto.newscategory;

import com.example.tileshop.entity.NewsCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsCategoryResponseDTO {

    private long id;
    private String name;

    public NewsCategoryResponseDTO(NewsCategory category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
