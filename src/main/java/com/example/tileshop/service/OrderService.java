package com.example.tileshop.service;

import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.order.OrderForUserResponseDTO;
import com.example.tileshop.dto.order.OrderRequestDTO;
import com.example.tileshop.dto.order.OrderResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;

import java.util.List;

public interface OrderService {
    PaginationResponseDTO<OrderResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    OrderResponseDTO findById(Long id);

    CommonResponseDTO updateStatus(Long id, OrderStatus status);

    List<OrderForUserResponseDTO> userFindAll(String userId, OrderStatus status, String keyword);

    OrderResponseDTO userFindById(Long id, String userId);

    CommonResponseDTO createOrder(OrderRequestDTO orderRequestDTO, String userId);
}