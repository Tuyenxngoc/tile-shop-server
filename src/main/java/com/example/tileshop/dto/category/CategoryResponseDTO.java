package com.example.tileshop.dto.category;

import com.example.tileshop.dto.common.BaseEntityDTO;
import com.example.tileshop.entity.Attribute;
import com.example.tileshop.entity.Category;
import com.example.tileshop.entity.CategoryAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CategoryResponseDTO {
    private long id;
    private String name;
    private BaseEntityDTO parent;
    private List<Long> attributeIds;

    public CategoryResponseDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.parent = category.getParent() != null ? new BaseEntityDTO(category.getParent().getId(), category.getParent().getName()) : null;
        this.attributeIds = category.getCategoryAttributes().stream()
                .map(CategoryAttribute::getAttribute)
                .map(Attribute::getId)
                .toList();
    }

}
