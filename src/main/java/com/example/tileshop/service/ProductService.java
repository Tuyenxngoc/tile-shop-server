package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.product.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface ProductService {
    CommonResponseDTO save(ProductRequestDTO requestDTO, List<MultipartFile> images);

    CommonResponseDTO update(Long id, ProductRequestDTO requestDTO, List<MultipartFile> images, Set<String> existingImageUrls);

    CommonResponseDTO delete(Long id);

    PaginationResponseDTO<ProductResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    ProductUpdateResponseDTO findById(Long id);

    PaginationResponseDTO<ProductForUserResponseDTO> userFindAll(PaginationFullRequestDTO requestDTO);

    ProductDetailResponseDTO userFindById(Long id);

    ProductDetailResponseDTO userFindBySlug(String slug);
}