package com.example.tileshop.service;

import com.example.tileshop.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    List<OrderItem> getAll();
}