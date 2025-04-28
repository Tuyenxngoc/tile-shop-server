package com.example.tileshop.service.impl;

import com.example.tileshop.repository.OrderItemRepository;
import com.example.tileshop.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderitemRepository;
}