package com.example.tileshop.service.impl;

import com.example.tileshop.config.VnPayConfig;
import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.constant.PaymentStatus;
import com.example.tileshop.dto.order.PaymentResponseDTO;
import com.example.tileshop.dto.order.PaymentStatusResponse;
import com.example.tileshop.entity.Order;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.OrderRepository;
import com.example.tileshop.service.VnPayService;
import com.example.tileshop.util.PaymentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnPayServiceImpl implements VnPayService {
    private static final Map<String, String> RESPONSE_CODE_MESSAGES = Map.ofEntries(
            Map.entry("00", "Giao dịch thành công"),
            Map.entry("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ"),
            Map.entry("09", "Thẻ/Tài khoản chưa đăng ký InternetBanking"),
            Map.entry("10", "Xác thực thông tin sai quá 3 lần"),
            Map.entry("11", "Hết thời gian thanh toán"),
            Map.entry("12", "Thẻ/Tài khoản bị khóa"),
            Map.entry("13", "Sai OTP"),
            Map.entry("24", "Khách hàng hủy giao dịch"),
            Map.entry("51", "Không đủ số dư"),
            Map.entry("65", "Vượt hạn mức giao dịch trong ngày"),
            Map.entry("75", "Ngân hàng bảo trì"),
            Map.entry("79", "Sai mật khẩu thanh toán quá số lần"),
            Map.entry("99", "Lỗi khác")
    );

    private final VnPayConfig vnPayConfig;

    private final OrderRepository orderRepository;

    @Override
    public PaymentResponseDTO createPaymentUrl(Long orderId, HttpServletRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Order.ERR_NOT_FOUND_ID, orderId));

        // Kiểm tra trạng thái thanh toán
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException(ErrorMessage.Order.ERR_ALREADY_PAID);
        }

        // Kiểm tra trạng thái đơn hàng đã huỷ chưa
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException(ErrorMessage.Order.ERR_ORDER_CANCELLED);
        }

        long amount = (long) (order.getTotalAmount() * 100);

        String vnp_Version = vnPayConfig.getVersion();
        String vnp_Command = vnPayConfig.getCommand();
        String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
        String orderType = request.getParameter("ordertype");
        String vnp_TxnRef = PaymentUtil.getRandomNumber(8);
        String vnp_IpAddr = PaymentUtil.getIpAddress(request);
        String vnp_TmnCode = vnPayConfig.getTmnCode();

        // Lưu thông tin giao dịch vào đơn hàng trước khi tạo URL thanh toán
        order.setTransactionId(vnp_TxnRef);
        orderRepository.save(order);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        String bank_code = request.getParameter("bankcode");
        if (bank_code != null && !bank_code.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bank_code);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        if (vnp_OrderInfo == null || vnp_OrderInfo.isEmpty()) {
            vnp_OrderInfo = "Thanh toan don hang: " + vnp_TxnRef;
        }
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        if (orderType == null || orderType.isEmpty()) {
            orderType = vnPayConfig.getOrderType();
        }
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = request.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        //Billing
        vnp_Params.put("vnp_Bill_Mobile", request.getParameter("txt_billing_mobile"));
        vnp_Params.put("vnp_Bill_Email", request.getParameter("txt_billing_email"));
        String fullName = (request.getParameter("txt_billing_fullname"));
        if (fullName != null && !fullName.isEmpty()) {
            fullName = fullName.trim();
            int idx = fullName.indexOf(' ');
            String firstName = fullName.substring(0, idx);
            String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
            vnp_Params.put("vnp_Bill_FirstName", firstName);
            vnp_Params.put("vnp_Bill_LastName", lastName);
        }

        vnp_Params.put("vnp_Bill_Address", request.getParameter("txt_inv_addr1"));
        vnp_Params.put("vnp_Bill_City", request.getParameter("txt_bill_city"));
        vnp_Params.put("vnp_Bill_Country", request.getParameter("txt_bill_country"));
        if (request.getParameter("txt_bill_state") != null && !request.getParameter("txt_bill_state").isEmpty()) {
            vnp_Params.put("vnp_Bill_State", request.getParameter("txt_bill_state"));
        }

        // Invoice
        vnp_Params.put("vnp_Inv_Phone", request.getParameter("txt_inv_mobile"));
        vnp_Params.put("vnp_Inv_Email", request.getParameter("txt_inv_email"));
        vnp_Params.put("vnp_Inv_Customer", request.getParameter("txt_inv_customer"));
        vnp_Params.put("vnp_Inv_Address", request.getParameter("txt_inv_addr1"));
        vnp_Params.put("vnp_Inv_Company", request.getParameter("txt_inv_company"));
        vnp_Params.put("vnp_Inv_Taxcode", request.getParameter("txt_inv_taxcode"));
        vnp_Params.put("vnp_Inv_Type", request.getParameter("cbo_inv_type"));

        //Build data to hash and query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPayConfig.getPayUrl() + "?" + queryUrl;

        return PaymentResponseDTO.builder()
                .status("ok")
                .message("success")
                .URL(paymentUrl).build();
    }

    @Override
    public PaymentStatusResponse handleVNPayReturn(String receivedHash, HttpServletRequest request) {
        Map<String, String> parameters = PaymentUtil.getParametersFromRequest(request);
        parameters.remove("vnp_SecureHash");
        String calculatedHash = PaymentUtil.hmacSHA512(vnPayConfig.getHashSecret(), PaymentUtil.buildHashData(parameters));

        if (!receivedHash.equalsIgnoreCase(calculatedHash)) {
            throw new BadRequestException(ErrorMessage.Order.ERR_INVALID_OR_TAMPERED_DATA);
        }

        String vnpTxnRef = parameters.get("vnp_TxnRef");
        String vnpResponseCode = parameters.get("vnp_ResponseCode");

        Order order = orderRepository.findByTransactionId(vnpTxnRef)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Order.ERR_NOT_FOUND_ID, vnpTxnRef));

        if ("00".equals(vnpResponseCode)) {
            order.setPaymentStatus(PaymentStatus.PAID);
            String vnpPayDate = parameters.get("vnp_PayDate");
            if (vnpPayDate != null && !vnpPayDate.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime payDateTime = LocalDateTime.parse(vnpPayDate, formatter);
                order.setPaymentTime(payDateTime);
            } else {
                order.setPaymentTime(LocalDateTime.now());
            }
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
        }

        order.setResponseCode(vnpResponseCode);
        orderRepository.save(order);

        String message = RESPONSE_CODE_MESSAGES.getOrDefault(vnpResponseCode, "Không xác định mã lỗi");

        return new PaymentStatusResponse(
                message,
                order.getId(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getTotalAmount()
        );
    }
}
