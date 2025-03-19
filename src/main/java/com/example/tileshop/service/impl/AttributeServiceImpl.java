package com.example.tileshop.service.impl;

import com.example.tileshop.entity.Attribute;
import com.example.tileshop.repository.AttributeRepository;
import com.example.tileshop.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {
    private final AttributeRepository attributeRepository;

    @Override
    public List<Attribute> getAll() {
        return attributeRepository.findAll();
    }
}