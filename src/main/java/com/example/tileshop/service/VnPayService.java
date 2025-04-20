package com.example.tileshop.service;

import com.example.tileshop.dto.order.PaymentResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.view.RedirectView;

public interface VnPayService {
    PaymentResponseDTO createPaymentUrl(Long amount, String bankCode, HttpServletRequest request);

    RedirectView handleVNPayReturn(HttpServletRequest request);
}
