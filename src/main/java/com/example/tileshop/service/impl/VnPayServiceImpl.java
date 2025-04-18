package com.example.tileshop.service.impl;

import com.example.tileshop.config.VnPayConfig;
import com.example.tileshop.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VnPayServiceImpl implements VnPayService {

    private final VnPayConfig config;

    @Override
    public String createPaymentUrl(HttpServletRequest request, long amount) {
        return "";
    }

}
