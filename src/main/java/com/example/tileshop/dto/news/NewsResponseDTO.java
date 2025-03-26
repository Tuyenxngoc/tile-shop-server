package com.example.tileshop.dto.news;

import com.example.tileshop.entity.News;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewsResponseDTO {

    private Long id;

    private String title;

    private String description;

    private String content;

    private String imageUrl;

    public NewsResponseDTO(News news) {
        this.id = news.getId();
        this.title = news.getTitle();
        this.description = news.getDescription();
        this.content = news.getContent();
        this.imageUrl = news.getImageUrl();
    }

}
