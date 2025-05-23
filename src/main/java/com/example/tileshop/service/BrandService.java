package com.example.tileshop.service;

import com.example.tileshop.dto.brand.BrandRequestDTO;
import com.example.tileshop.dto.brand.BrandResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface BrandService {
    void init();

    CommonResponseDTO save(BrandRequestDTO requestDTO, MultipartFile image);

    CommonResponseDTO update(Long id, BrandRequestDTO requestDTO, MultipartFile image);

    CommonResponseDTO delete(Long id);

    PaginationResponseDTO<BrandResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    BrandResponseDTO findById(Long id);

    BrandResponseDTO findBySlug(String slug);
}