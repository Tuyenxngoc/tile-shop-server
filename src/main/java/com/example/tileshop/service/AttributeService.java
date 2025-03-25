package com.example.tileshop.service;

import com.example.tileshop.dto.attribute.AttributeRequestDTO;
import com.example.tileshop.dto.attribute.AttributeResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;

public interface AttributeService {
    CommonResponseDTO save(AttributeRequestDTO requestDTO);

    CommonResponseDTO update(Long id, AttributeRequestDTO requestDTO);

    CommonResponseDTO delete(Long id);

    PaginationResponseDTO<AttributeResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    AttributeResponseDTO findById(Long id);
}