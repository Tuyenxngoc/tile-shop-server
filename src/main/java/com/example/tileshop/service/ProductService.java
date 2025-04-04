package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.product.ProductRequestDTO;
import com.example.tileshop.dto.product.ProductResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    CommonResponseDTO save(ProductRequestDTO requestDTO, List<MultipartFile> images);

    CommonResponseDTO update(Long id, ProductRequestDTO requestDTO, List<MultipartFile> images);

    CommonResponseDTO delete(Long id);

    PaginationResponseDTO<ProductResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    ProductRequestDTO findById(Long id);

    Object findBySlug(String slug);
}