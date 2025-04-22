package com.example.tileshop.service;

import com.example.tileshop.dto.order.PaymentResponseDTO;
import com.example.tileshop.dto.order.PaymentStatusResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface VnPayService {
    PaymentResponseDTO createPaymentUrl(Long orderId, HttpServletRequest request);

    PaymentStatusResponse handleVNPayReturn(String receivedHash, HttpServletRequest request);
}
