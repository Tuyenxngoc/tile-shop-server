package com.example.tileshop.dto.category;

import com.example.tileshop.dto.common.DateAuditingDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO extends DateAuditingDTO {
    private long id;

    private String name;

    private String slug;

    private String description;

    private String imageUrl;

    private CategorySimpleDTO parent;

    private List<Long> attributeIds;
}
