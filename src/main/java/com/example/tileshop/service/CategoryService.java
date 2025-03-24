package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDto;
import com.example.tileshop.dto.request.CategoryRequestDto;
import com.example.tileshop.dto.request.pagination.PaginationFullRequestDto;

public interface CategoryService {
    CommonResponseDto save(CategoryRequestDto requestDto);

    CommonResponseDto update(Long id, CategoryRequestDto requestDto);

    CommonResponseDto delete(Long id);

    Object findAll(PaginationFullRequestDto requestDto);

    Object findById(Long id);
}