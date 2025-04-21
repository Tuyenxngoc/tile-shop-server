package com.example.tileshop.service;

import com.example.tileshop.dto.order.OrderRequestDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import jakarta.validation.Valid;

public interface OrderService {
    Object findAll(PaginationFullRequestDTO requestDTO);

    Object findById(Long id);

    Object updateStatus(Long id, String status);

    Object userFindAll(PaginationFullRequestDTO requestDTO);

    Object userFindById(Long id);

    Object createOrder(@Valid OrderRequestDTO orderRequestDTO, String userId);
}