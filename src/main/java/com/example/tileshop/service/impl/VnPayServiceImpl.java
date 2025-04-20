package com.example.tileshop.service.impl;

import com.example.tileshop.config.VnPayConfig;
import com.example.tileshop.dto.order.PaymentResponseDTO;
import com.example.tileshop.service.VnPayService;
import com.example.tileshop.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VnPayServiceImpl implements VnPayService {

    private final VnPayConfig vnPayConfig;

    @Override
    public PaymentResponseDTO createPaymentUrl(Long amount, String bankCode, HttpServletRequest request) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        long amountInVNPayFormat = amount * 100L;

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amountInVNPayFormat));

        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }

        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getPayUrl() + "?" + queryUrl;

        return PaymentResponseDTO.builder()
                .status("ok")
                .message("success")
                .URL(paymentUrl).build();
    }


    @Override
    public RedirectView handleVNPayReturn(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        RedirectView redirectView = new RedirectView();
        String status = responseCode.equals("00") ? "success" : "fail";

        String url = "http://localhost:3000/order-status"
                + "?status=" + status;

        if (responseCode.equals("00")) {
            url += "&txnRef=" + request.getParameter("vnp_TxnRef")
                    + "&transactionNo=" + request.getParameter("vnp_TransactionNo")
                    + "&amount=" + request.getParameter("vnp_Amount")
                    + "&payDate=" + request.getParameter("vnp_PayDate");
        }

        redirectView.setUrl(url);
        return redirectView;
    }

    @Transactional
    public void handleVNPayRefund(long orderId, HttpServletRequest request) {

    }

}
