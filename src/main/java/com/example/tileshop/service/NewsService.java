package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.news.NewsRequestDTO;
import com.example.tileshop.dto.news.NewsResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface NewsService {
    CommonResponseDTO save(NewsRequestDTO requestDTO, MultipartFile image);

    CommonResponseDTO update(Long id, NewsRequestDTO requestDTO, MultipartFile image);

    CommonResponseDTO delete(Long id);

    PaginationResponseDTO<NewsResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    NewsResponseDTO findById(Long id);

    NewsResponseDTO findBySlug(String slug);
}