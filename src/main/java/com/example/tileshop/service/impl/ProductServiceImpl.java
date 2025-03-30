package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.product.ProductRequestDTO;
import com.example.tileshop.entity.Product;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.News.ERR_NOT_FOUND_ID, id));
    }

    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int counter = 1;

        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    @Override
    public CommonResponseDTO save(ProductRequestDTO requestDTO, List<MultipartFile> images) {
        return null;
    }

    @Override
    public CommonResponseDTO update(Long id, ProductRequestDTO requestDTO, List<MultipartFile> images) {
        return null;
    }

    @Override
    public CommonResponseDTO delete(Long id) {
        return null;
    }

    @Override
    public Object findAll(PaginationFullRequestDTO requestDTO) {
        return null;
    }

    @Override
    public Object findById(Long id) {
        return null;
    }

    @Override
    public Object findBySlug(String slug) {
        return null;
    }
}