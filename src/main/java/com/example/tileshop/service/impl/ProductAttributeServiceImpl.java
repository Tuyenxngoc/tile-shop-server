package com.example.tileshop.service.impl;

import com.example.tileshop.entity.ProductAttribute;
import com.example.tileshop.repository.ProductAttributeRepository;
import com.example.tileshop.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAttributeServiceImpl implements ProductAttributeService {
    private final ProductAttributeRepository productattributeRepository;

    @Override
    public List<ProductAttribute> getAll() {
        return productattributeRepository.findAll();
    }
}