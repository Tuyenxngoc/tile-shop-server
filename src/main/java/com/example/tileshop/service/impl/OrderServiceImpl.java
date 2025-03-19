package com.example.tileshop.service.impl;

import com.example.tileshop.entity.Order;
import com.example.tileshop.repository.OrderRepository;
import com.example.tileshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }
}