package com.example.tileshop.service.impl;

import com.example.tileshop.entity.CategoryAttribute;
import com.example.tileshop.repository.CategoryAttributeRepository;
import com.example.tileshop.service.CategoryAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryAttributeServiceImpl implements CategoryAttributeService {
    private final CategoryAttributeRepository categoryattributeRepository;

    @Override
    public List<CategoryAttribute> getAll() {
        return categoryattributeRepository.findAll();
    }
}