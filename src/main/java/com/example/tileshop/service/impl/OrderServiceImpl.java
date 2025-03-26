package com.example.tileshop.service.impl;

import com.example.tileshop.repository.OrderRepository;
import com.example.tileshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

}