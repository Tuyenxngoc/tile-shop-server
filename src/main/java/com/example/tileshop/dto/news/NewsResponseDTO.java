package com.example.tileshop.dto.news;

import com.example.tileshop.dto.common.BaseEntityDTO;
import com.example.tileshop.dto.common.DateAuditingDTO;
import com.example.tileshop.entity.News;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewsResponseDTO extends DateAuditingDTO {

    private long id;

    private String title;

    private String slug;

    private String description;

    private String content;

    private String imageUrl;

    private BaseEntityDTO category;

    public NewsResponseDTO(News news) {
        this.createdDate = news.getCreatedDate();
        this.lastModifiedDate = news.getLastModifiedDate();
        this.id = news.getId();
        this.title = news.getTitle();
        this.slug = news.getSlug();
        this.description = news.getDescription();
        this.content = news.getContent();
        this.imageUrl = news.getImageUrl();
        this.category = new BaseEntityDTO(news.getCategory().getId(), news.getCategory().getName());
    }

}
