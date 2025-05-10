package com.example.tileshop.service;

import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.filter.OrderFilterRequestDTO;
import com.example.tileshop.dto.order.CancelOrderRequestDTO;
import com.example.tileshop.dto.order.OrderRequestDTO;
import com.example.tileshop.dto.order.OrderResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.security.CustomUserDetails;

import java.util.List;
import java.util.Map;

public interface OrderService {
    PaginationResponseDTO<OrderResponseDTO> findAll(OrderFilterRequestDTO filter, PaginationFullRequestDTO requestDTO);

    OrderResponseDTO findById(Long id);

    CommonResponseDTO updateStatus(Long id, OrderStatus status);

    Map<String, Long> countOrdersByStatus();

    List<OrderResponseDTO> userFindAll(String userId, OrderStatus status, String keyword);

    OrderResponseDTO userFindById(Long id, CustomUserDetails userDetails);

    CommonResponseDTO create(OrderRequestDTO orderRequestDTO, String userId);

    CommonResponseDTO cancelOrder(Long id, CancelOrderRequestDTO requestDTO, CustomUserDetails userDetails);

    byte[] generateOrderReport(OrderFilterRequestDTO filter);
}