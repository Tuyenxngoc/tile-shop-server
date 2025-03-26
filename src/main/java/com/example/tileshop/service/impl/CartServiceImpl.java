package com.example.tileshop.service.impl;

import com.example.tileshop.repository.CartRepository;
import com.example.tileshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

}