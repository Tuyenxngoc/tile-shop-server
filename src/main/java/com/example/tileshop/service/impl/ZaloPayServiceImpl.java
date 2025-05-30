package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.constant.PaymentStatus;
import com.example.tileshop.dto.order.PaymentResponseDTO;
import com.example.tileshop.dto.order.PaymentStatusResponse;
import com.example.tileshop.entity.Order;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.OrderRepository;
import com.example.tileshop.service.ZaloPayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZaloPayServiceImpl implements ZaloPayService {

    @Value("${zalopay.app_id}")
    private String appId;

    @Value("${zalopay.key1}")
    private String key1;

    @Value("${zalopay.key2}")
    private String key2;

    @Value("${zalopay.create_order_url}")
    private String createOrderUrlApi;

    @Value("${zalopay.callback_url}")
    private String configuredCallbackUrl;

    @Value("${zalopay.redirect_url}")
    private String configuredRedirectUrl;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    private final OrderRepository orderRepository;

    // Helper để chuyển byte array sang hex string
    private String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        try (Formatter formatter = new Formatter(sb)) {
            for (byte b : bytes) {
                formatter.format("%02x", b);
            }
        }
        return sb.toString();
    }

    // Helper tạo HMAC SHA256
    private String hmacSha256(String key, String data) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKey);
            byte[] hash = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHexString(hash);
        } catch (Exception e) {
            log.error("Lỗi tạo HMAC SHA256: {}", e.getMessage());
            throw new RuntimeException("Lỗi tạo HMAC SHA256", e);
        }
    }

    @Override
    @Transactional
    public PaymentResponseDTO createPaymentUrl(Long orderId, HttpServletRequest cc) {
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

        try {
            // app_trans_id: Mã giao dịch của ứng dụng, phải là duy nhất cho mỗi lần tạo đơn hàng.
            // Format: yymmdd_UNIQUEID. Độ dài tối đa 40 ký tự.
            // UUID.randomUUID().toString().replace("-", "") tạo ra 32 ký tự.
            // "yyMMdd_" (7 ký tự) + 32 ký tự = 39 ký tự.
            String appTransId = new SimpleDateFormat("yyMMdd").format(new Date()) + "_" + UUID.randomUUID().toString().replace("-", "");
            String appTime = String.valueOf(System.currentTimeMillis());
            String appUser = "user123";

            List<Map<String, Object>> itemList = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("itemid", "knb");
            item.put("itename", "kim nguyen bao");
            item.put("itemprice", 198400);
            item.put("itemquantity", 1);
            itemList.add(item);
            String items = objectMapper.writeValueAsString(itemList); // Convert to JSON string

            long amount = Math.round(order.getTotalAmount());
            String description = "Thanh toan don hang: " + order.getId();

            Map<String, String> embedDataMap = new HashMap<>();
            embedDataMap.put("redirecturl", configuredRedirectUrl); // URL ZaloPay redirect người dùng về sau thanh toán
            // embedDataMap.put("merchantinfo", "Thông tin thêm của merchant"); // Dữ liệu tùy chỉnh khác (nếu có)
            String embedData = objectMapper.writeValueAsString(embedDataMap);

            // Chuỗi dữ liệu để tạo MAC với key1
            String macData = appId + "|" + appTransId + "|" + appUser + "|" + amount + "|" + appTime + "|" + embedData + "|" + items;
            String mac = hmacSha256(key1, macData);

            Map<String, Object> orderRequestPayload = new HashMap<>();
            orderRequestPayload.put("app_id", appId);
            orderRequestPayload.put("app_user", appUser);
            orderRequestPayload.put("app_trans_id", appTransId);
            orderRequestPayload.put("app_time", Long.parseLong(appTime));
            orderRequestPayload.put("amount", amount);
            orderRequestPayload.put("item", itemList);
            orderRequestPayload.put("description", description);
            orderRequestPayload.put("embed_data", embedData);
            orderRequestPayload.put("bank_code", "");
            orderRequestPayload.put("mac", mac);
            orderRequestPayload.put("callback_url", configuredCallbackUrl);

            String jsonPayload = objectMapper.writeValueAsString(orderRequestPayload);
            log.info("ZaloPay Create Order Request: {}", jsonPayload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(createOrderUrlApi))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("ZaloPay Create Order Response: {}", response.body());

            JsonNode responseNode = objectMapper.readTree(response.body());
            int returnCode = responseNode.get("return_code").asInt();
            String returnMessage = responseNode.get("return_message").asText();

            if (returnCode == 1) { // Tạo đơn hàng thành công
                String orderUrl = responseNode.get("order_url").asText();
                String zpTransToken = responseNode.has("zp_trans_token") ? responseNode.get("zp_trans_token").asText() : null;

                order.setTransactionId(appTransId);
                orderRepository.save(order);

                log.info("Tạo đơn hàng ZaloPay thành công cho app_trans_id: {}. URL: {}", appTransId, orderUrl);
                return PaymentResponseDTO.builder()
                        .status("ok")
                        .message("success")
                        .URL(orderUrl).build();
            } else {
                String subReturnMessage = responseNode.has("sub_return_message") ? responseNode.get("sub_return_message").asText() : returnMessage;
                log.error("Tạo đơn hàng ZaloPay thất bại: Mã lỗi {}, Tin nhắn: {}, Chi tiết: {}", returnCode, returnMessage, subReturnMessage);
                return PaymentResponseDTO.builder()
                        .status("false")
                        .message("Tạo đơn hàng ZaloPay thất bại: " + subReturnMessage)
                        .URL(null).build();
            }
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi tạo URL thanh toán ZaloPay: {}", e.getMessage(), e);
            return PaymentResponseDTO.builder()
                    .status("false")
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .URL(null).build();
        }
    }

    @Override
    @Transactional
    public PaymentStatusResponse handleZaloPayReturn(HttpServletRequest request) {
        // Phương thức này xử lý IPN (callback) từ ZaloPay.
        // ZaloPay gửi dữ liệu callback dưới dạng JSON trong request body.
        // JSON này chứa 2 trường chính: "data" (một chuỗi JSON khác) và "mac".
        String appTransId = null; // Khởi tạo để sử dụng trong logging và response DTO
        try {
            String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.info("ZaloPay Callback Received (IPN): {}", requestBody);

            JsonNode callbackJson = objectMapper.readTree(requestBody);
            String dataStr = callbackJson.get("data").asText(); // Đây là một chuỗi JSON
            String receivedMac = callbackJson.get("mac").asText();

            // **QUAN TRỌNG**: Xác minh MAC với key2.
            // Theo tài liệu ZaloPay (Payment Gateway V2 > Callback), chuỗi dữ liệu để tạo HMAC là chính trường "data".
            String calculatedMac = hmacSha256(key2, dataStr);

            if (!calculatedMac.equals(receivedMac)) {
                log.warn("ZaloPay Callback: Xác thực MAC thất bại. MAC nhận được: {}, MAC tính toán: {}", receivedMac, calculatedMac);
                // Theo ZaloPay, nếu MAC sai, không nên xử lý và có thể không phản hồi hoặc phản hồi lỗi.
                ObjectNode responseToZalo = objectMapper.createObjectNode();
                responseToZalo.put("return_code", -1); // Mã lỗi cho ZaloPay (tham khảo tài liệu)
                responseToZalo.put("return_message", "MAC verification failed");
                //  return new PaymentStatusResponse(false, "MAC_INVALID", "Xác thực MAC thất bại.", null, null, responseToZalo.toString());
            }

            log.info("ZaloPay Callback: Xác thực MAC thành công.");
            JsonNode dataNode = objectMapper.readTree(dataStr); // Parse chuỗi JSON "data"

            appTransId = dataNode.get("app_trans_id").asText();
            String zpTransId = dataNode.get("zp_trans_id").asText(); // Mã giao dịch của ZaloPay
            long amount = dataNode.get("amount").asLong();
            // long serverTime = dataNode.get("server_time").asLong(); // Thời gian phía ZaloPay, hoặc "timestamp" - kiểm tra tài liệu
            int status = dataNode.get("status").asInt(); // Trạng thái thanh toán: 1 = thành công, 2 = thất bại, 3 = đang chờ

            // TODO: Xử lý trạng thái thanh toán
            // 1. Tìm đơn hàng của bạn bằng app_trans_id
            // Order order = orderRepository.findByZaloPayAppTransId(appTransId).orElse(null);
            // if (order == null) {
            // log.warn("ZaloPay Callback: Không tìm thấy đơn hàng cho app_trans_id: {}", appTransId);
            // ObjectNode responseToZalo = objectMapper.createObjectNode();
            // responseToZalo.put("return_code", 0); // Hoặc mã lỗi phù hợp
            // responseToZalo.put("return_message", "Order not found");
            // return new PaymentStatusResponse(false, "ORDER_NOT_FOUND", "Không tìm thấy đơn hàng.", appTransId, null, responseToZalo.toString());
            // }

            // 2. Kiểm tra xem đơn hàng đã được xử lý trước đó chưa để tránh xử lý trùng lặp
            // if (order.getOrderStatus() == OrderStatus.PAID || order.getOrderStatus() == OrderStatus.PAYMENT_FAILED) {
            // log.info("ZaloPay Callback: Đơn hàng {} đã được xử lý với trạng thái {}", appTransId, order.getOrderStatus());
            // ObjectNode responseToZalo = objectMapper.createObjectNode();
            // responseToZalo.put("return_code", 1); // Phản hồi thành công vì đã xử lý
            // responseToZalo.put("return_message", "success");
            // return new PaymentStatusResponse(true, "ALREADY_PROCESSED", "Đơn hàng đã được xử lý.", appTransId, order.getId().toString(), responseToZalo.toString());
            // }

            String finalOrderStatus;
            ObjectNode responseToZalo = objectMapper.createObjectNode();

            if (status == 1) { // Thanh toán thành công
                log.info("ZaloPay Callback: Thanh toán thành công cho app_trans_id: {}", appTransId);
                // order.setOrderStatus(OrderStatus.PAID);
                // order.setZaloPayZpTransId(zpTransId); // Lưu mã giao dịch của ZaloPay
                // order.setPaidAt(new Date(serverTime)); // Hoặc new Date()
                finalOrderStatus = "PAID";
                responseToZalo.put("return_code", 1); // Phản hồi thành công cho ZaloPay
                responseToZalo.put("return_message", "success");
            } else if (status == 2) { // Thanh toán thất bại
                log.warn("ZaloPay Callback: Thanh toán thất bại cho app_trans_id: {}. Trạng thái ZaloPay: {}", appTransId, status);
                // order.setOrderStatus(OrderStatus.PAYMENT_FAILED);
                finalOrderStatus = "FAILED";
                responseToZalo.put("return_code", 2); // Phản hồi thất bại cho ZaloPay (tham khảo tài liệu)
                responseToZalo.put("return_message", "payment failed");
            } else if (status == 3) { // Thanh toán đang chờ (ít gặp ở callback cuối cùng, nhưng có thể)
                log.info("ZaloPay Callback: Thanh toán đang chờ cho app_trans_id: {}. Trạng thái ZaloPay: {}", appTransId, status);
                // order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
                finalOrderStatus = "PENDING";
                responseToZalo.put("return_code", 1); // Có thể phản hồi success để ZaloPay biết đã nhận được
                responseToZalo.put("return_message", "pending");
            } else {
                log.warn("ZaloPay Callback: Trạng thái thanh toán không xác định {} cho app_trans_id: {}", status, appTransId);
                // order.setOrderStatus(OrderStatus.PAYMENT_UNKNOWN);
                finalOrderStatus = "UNKNOWN_STATUS_" + status;
                responseToZalo.put("return_code", 0); // Hoặc mã lỗi phù hợp
                responseToZalo.put("return_message", "unknown status");
            }

            // orderRepository.save(order); // Lưu thay đổi vào DB
            log.info("ZaloPay Callback: Phản hồi cho ZaloPay: {}", responseToZalo.toString());

            // PaymentStatusResponse chứa thông tin cho logic nội bộ và cả chuỗi JSON để phản hồi ZaloPay
            //  return new PaymentStatusResponse(true, finalOrderStatus, "Callback đã được xử lý.", appTransId, null /* order.getId().toString() */, responseToZalo.toString());

        } catch (JsonProcessingException e) {
            log.error("ZaloPay Callback: Lỗi xử lý JSON: {}", e.getMessage(), e);
            ObjectNode errorResponseToZalo = objectMapper.createObjectNode();
            errorResponseToZalo.put("return_code", -2); // Mã lỗi cho ZaloPay
            errorResponseToZalo.put("return_message", "Invalid data format");
            //    return new PaymentStatusResponse(false, "INVALID_JSON", "Dữ liệu JSON không hợp lệ trong callback.", appTransId, null, errorResponseToZalo.toString());
        } catch (IOException e) {
            log.error("ZaloPay Callback: Lỗi IO khi đọc request: {}", e.getMessage(), e);
            ObjectNode errorResponseToZalo = objectMapper.createObjectNode();
            errorResponseToZalo.put("return_code", -3);
            errorResponseToZalo.put("return_message", "IO error");
            //  return new PaymentStatusResponse(false, "IO_ERROR", "Lỗi đọc callback request.", appTransId, null, errorResponseToZalo.toString());
        } catch (Exception e) {
            log.error("ZaloPay Callback: Lỗi không mong muốn: {}", e.getMessage(), e);
            ObjectNode errorResponseToZalo = objectMapper.createObjectNode();
            errorResponseToZalo.put("return_code", -4);
            errorResponseToZalo.put("return_message", "Internal server error");
            //    return new PaymentStatusResponse(false, "GENERAL_ERROR", "Đã xảy ra lỗi không mong muốn.", appTransId, null, errorResponseToZalo.toString());
        }
        return null;
    }
}
