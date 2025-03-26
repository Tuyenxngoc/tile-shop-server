package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.newscategory.NewsCategoryRequestDTO;
import com.example.tileshop.dto.newscategory.NewsCategoryResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;

public interface NewsCategoryService {
    CommonResponseDTO save(NewsCategoryRequestDTO requestDTO);

    CommonResponseDTO update(Long id, NewsCategoryRequestDTO requestDTO);

    CommonResponseDTO delete(Long id);

    PaginationResponseDTO<NewsCategoryResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    NewsCategoryResponseDTO findById(Long id);
}