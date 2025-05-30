package com.example.tileshop.service;

import com.example.tileshop.dto.order.PaymentResponseDTO;
import com.example.tileshop.dto.order.PaymentStatusResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface ZaloPayService {
    PaymentResponseDTO createPaymentUrl(Long orderId, HttpServletRequest request);

    PaymentStatusResponse handleZaloPayReturn(HttpServletRequest request);
}
