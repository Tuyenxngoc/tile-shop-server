package com.example.tileshop.service.impl;

import com.example.tileshop.entity.OrderItem;
import com.example.tileshop.repository.OrderItemRepository;
import com.example.tileshop.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderitemRepository;

    @Override
    public List<OrderItem> getAll() {
        return orderitemRepository.findAll();
    }
}