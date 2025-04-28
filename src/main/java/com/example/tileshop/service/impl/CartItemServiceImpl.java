package com.example.tileshop.service.impl;

import com.example.tileshop.repository.CartItemRepository;
import com.example.tileshop.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartitemRepository;
}