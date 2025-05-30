package com.example.tileshop.dto.news;

import com.example.tileshop.dto.common.DateAuditingDTO;
import com.example.tileshop.dto.newscategory.NewsCategoryResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponseDTO extends DateAuditingDTO {
    private long id;

    private String title;

    private String slug;

    private String description;

    private String content;

    private String imageUrl;

    private long viewCount;

    private NewsCategoryResponseDTO category;
}
