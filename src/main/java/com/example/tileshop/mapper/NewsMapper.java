package com.example.tileshop.mapper;

import com.example.tileshop.dto.news.NewsRequestDTO;
import com.example.tileshop.dto.news.NewsResponseDTO;
import com.example.tileshop.entity.News;

public class NewsMapper {
    public static NewsResponseDTO toDTO(News news) {
        if (news == null) {
            return null;
        }

        NewsResponseDTO dto = new NewsResponseDTO();
        // TODO: set fields từ News vào dto
        // vd: dto.setId(news.getId());

        return dto;
    }

    public static News toEntity(NewsRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setSlug(dto.getSlug());
        news.setDescription(dto.getDescription());
        news.setContent(dto.getContent());

        return news;
    }
}