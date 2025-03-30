package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.product.ProductRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    CommonResponseDTO save(ProductRequestDTO requestDTO, List<MultipartFile> images);

    CommonResponseDTO update(Long id, ProductRequestDTO requestDTO, List<MultipartFile> images);

    CommonResponseDTO delete(Long id);

    Object findAll(PaginationFullRequestDTO requestDTO);

    Object findById(Long id);

    Object findBySlug(String slug);
}