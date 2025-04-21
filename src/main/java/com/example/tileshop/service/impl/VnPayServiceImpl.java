package com.example.tileshop.service.impl;

import com.example.tileshop.config.VnPayConfig;
import com.example.tileshop.dto.order.PaymentResponseDTO;
import com.example.tileshop.service.VnPayService;
import com.example.tileshop.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VnPayServiceImpl implements VnPayService {

    private final VnPayConfig vnPayConfig;

   /** protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = req.getParameter("vnp_OrderInfo");
        String orderType = req.getParameter("ordertype");
        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = Config.getIpAddress(req);
        String vnp_TmnCode = Config.vnp_TmnCode;

        int amount = Integer.parseInt(req.getParameter("amount")) * 100;
        Map vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        String bank_code = req.getParameter("bankcode");
        if (bank_code != null && !bank_code.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bank_code);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = req.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        //Billing
        vnp_Params.put("vnp_Bill_Mobile", req.getParameter("txt_billing_mobile"));
        vnp_Params.put("vnp_Bill_Email", req.getParameter("txt_billing_email"));
        String fullName = (req.getParameter("txt_billing_fullname")).trim();
        if (fullName != null && !fullName.isEmpty()) {
            int idx = fullName.indexOf(' ');
            String firstName = fullName.substring(0, idx);
            String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
            vnp_Params.put("vnp_Bill_FirstName", firstName);
            vnp_Params.put("vnp_Bill_LastName", lastName);

        }
        vnp_Params.put("vnp_Bill_Address", req.getParameter("txt_inv_addr1"));
        vnp_Params.put("vnp_Bill_City", req.getParameter("txt_bill_city"));
        vnp_Params.put("vnp_Bill_Country", req.getParameter("txt_bill_country"));
        if (req.getParameter("txt_bill_state") != null && !req.getParameter("txt_bill_state").isEmpty()) {
            vnp_Params.put("vnp_Bill_State", req.getParameter("txt_bill_state"));
        }
        // Invoice
        vnp_Params.put("vnp_Inv_Phone", req.getParameter("txt_inv_mobile"));
        vnp_Params.put("vnp_Inv_Email", req.getParameter("txt_inv_email"));
        vnp_Params.put("vnp_Inv_Customer", req.getParameter("txt_inv_customer"));
        vnp_Params.put("vnp_Inv_Address", req.getParameter("txt_inv_addr1"));
        vnp_Params.put("vnp_Inv_Company", req.getParameter("txt_inv_company"));
        vnp_Params.put("vnp_Inv_Taxcode", req.getParameter("txt_inv_taxcode"));
        vnp_Params.put("vnp_Inv_Type", req.getParameter("cbo_inv_type"));
        //Build data to hash and querystring
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
        com.google.gson.JsonObject job = new JsonObject();
        job.addProperty("code", "00");
        job.addProperty("message", "success");
        job.addProperty("data", paymentUrl);
        Gson gson = new Gson();
        resp.getWriter().write(gson.toJson(job));
    }**/

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
    public void handleVNPayReturn(HttpServletRequest request) {
        // Lấy các tham số từ request
        String vnp_TxnRef = request.getParameter("vnp_TxnRef"); // Mã giao dịch của VNPay
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode"); // Mã phản hồi từ VNPay
        String vnp_SecureHash = request.getParameter("vnp_SecureHash"); // Chữ ký (hash) của VNPay
        String vnp_Amount = request.getParameter("vnp_Amount"); // Số tiền giao dịch
        String vnp_BankCode = request.getParameter("vnp_BankCode"); // Mã ngân hàng nếu có

        // Tạo lại hash để kiểm tra tính toàn vẹn của dữ liệu
        Map<String, String> vnpParamsMap = VNPayUtil.getParametersFromRequest(request);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false); // Lấy dữ liệu hash từ các tham số
        String secureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData); // Tính toán hash mới

        // Kiểm tra chữ ký (Secure Hash) có đúng không
        if (!vnp_SecureHash.equals(secureHash)) {
            // Nếu chữ ký không khớp, ghi log và thông báo lỗi
            throw new IllegalArgumentException("Invalid secure hash");
        }

        // Kiểm tra mã phản hồi từ VNPay
        if ("00".equals(vnp_ResponseCode)) {
            // Nếu mã phản hồi là 00, thanh toán thành công
            // Cập nhật trạng thái thanh toán thành công trong hệ thống
            // Ví dụ: Cập nhật trong cơ sở dữ liệu
            System.out.println("Payment successful, TxnRef: " + vnp_TxnRef);

            // Bạn có thể tiếp tục xử lý giao dịch thanh toán ở đây (cập nhật cơ sở dữ liệu, gửi thông báo, v.v.)
        } else {
            // Nếu mã phản hồi không phải 00, thanh toán thất bại
            // Ví dụ: Cập nhật trạng thái thanh toán thất bại trong hệ thống
            System.out.println("Payment failed, ResponseCode: " + vnp_ResponseCode);
        }
    }

}
