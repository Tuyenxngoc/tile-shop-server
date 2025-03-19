package com.example.tileshop.service.impl;

import com.example.tileshop.domain.entity.Product;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }
}