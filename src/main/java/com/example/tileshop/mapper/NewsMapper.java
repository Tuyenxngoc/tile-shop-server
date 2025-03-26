package com.example.tileshop.mapper;

import com.example.tileshop.dto.news.NewsRequestDTO;
import com.example.tileshop.entity.News;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    News toNews(NewsRequestDTO requestDTO);
}