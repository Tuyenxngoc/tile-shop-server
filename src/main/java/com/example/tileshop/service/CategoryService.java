package com.example.tileshop.service;

import com.example.tileshop.dto.category.CategoryRequestDTO;
import com.example.tileshop.dto.category.CategoryResponseDTO;
import com.example.tileshop.dto.category.CategoryTreeResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;

import java.util.List;

public interface CategoryService {
    void init();

    CommonResponseDTO save(CategoryRequestDTO requestDTO);

    CommonResponseDTO update(Long id, CategoryRequestDTO requestDTO);

    CommonResponseDTO delete(Long id);

    PaginationResponseDTO<CategoryResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    CategoryResponseDTO findById(Long id);

    List<CategoryTreeResponseDTO> getCategoriesTree();
}