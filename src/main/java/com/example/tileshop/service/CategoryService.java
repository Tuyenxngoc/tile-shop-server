package com.example.tileshop.service;

import com.example.tileshop.dto.category.CategoryRequestDto;
import com.example.tileshop.dto.category.CategoryResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDto;
import com.example.tileshop.dto.request.pagination.PaginationFullRequestDto;
import com.example.tileshop.dto.response.pagination.PaginationResponseDto;

public interface CategoryService {
    CommonResponseDto save(CategoryRequestDto requestDto);

    CommonResponseDto update(Long id, CategoryRequestDto requestDto);

    CommonResponseDto delete(Long id);

    PaginationResponseDto<CategoryResponseDTO> findAll(PaginationFullRequestDto requestDto);

    CategoryResponseDTO findById(Long id);
}