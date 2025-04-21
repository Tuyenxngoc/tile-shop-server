package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.order.OrderRequestDTO;
import com.example.tileshop.dto.order.OrderResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface OrderService {
    PaginationResponseDTO<OrderResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    OrderResponseDTO findById(Long id);

    CommonResponseDTO updateStatus(Long id, String status);

    List<OrderResponseDTO> userFindAll(PaginationFullRequestDTO requestDTO, String userId);

    OrderResponseDTO userFindById(Long id, String userId);

    CommonResponseDTO createOrder(@Valid OrderRequestDTO orderRequestDTO, String userId);
}