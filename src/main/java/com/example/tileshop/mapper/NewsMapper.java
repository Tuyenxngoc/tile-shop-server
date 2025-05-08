package com.example.tileshop.mapper;

import com.example.tileshop.dto.common.BaseEntityDTO;
import com.example.tileshop.dto.news.NewsRequestDTO;
import com.example.tileshop.dto.news.NewsResponseDTO;
import com.example.tileshop.entity.News;

public class NewsMapper {
    public static NewsResponseDTO toDTO(News news) {
        if (news == null) {
            return null;
        }

        NewsResponseDTO dto = new NewsResponseDTO();
        dto.setCreatedDate(news.getCreatedDate());
        dto.setLastModifiedDate(news.getLastModifiedDate());
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setSlug(news.getSlug());
        dto.setDescription(news.getDescription());
        dto.setContent(news.getContent());
        dto.setImageUrl(news.getImageUrl());
        dto.setViewCount(news.getViewCount());
        dto.setCategory(new BaseEntityDTO(news.getCategory().getId(), news.getCategory().getName()));

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