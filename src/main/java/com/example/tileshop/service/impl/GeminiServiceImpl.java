package com.example.tileshop.service.impl;

import com.example.tileshop.dto.chat.ChatRequestDTO;
import com.example.tileshop.entity.Product;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.service.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {
    private static final Logger log = LoggerFactory.getLogger(GeminiServiceImpl.class);
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    private final ProductRepository productRepository;

    @Override
    public String askGemini(ChatRequestDTO requestDTO) {
        String fullUrl = apiUrl + "?key=" + apiKey;

        try {
            // 1. Lọc sản phẩm liên quan
            String userMessage = requestDTO.getMessage();
            List<Product> matchedProducts = findRelevantProductInfo(userMessage);

            // 2. Tạo phần mô tả sản phẩm
            String productInfo = buildProductDescription(matchedProducts);

            // 3. Tạo request body gửi Gemini
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "contents", new Object[]{
                            Map.of(
                                    "role", "user",
                                    "parts", new Object[]{
                                            Map.of("text", "Dữ liệu sản phẩm của cửa hàng:\n" + productInfo),
                                            Map.of("text", "Câu hỏi khách hàng: " + userMessage)
                                    }
                            )
                    },
                    "generationConfig", Map.of(
                            "temperature", 0.9,        // Độ sáng tạo (0-1)
                            "topP", 0.8,               // Đa dạng hóa response
                            "topK", 40,                // Số lựa chọn
                            "maxOutputTokens", 2048,   // Giới hạn token
                            "stopSequences", new String[]{} // Chuỗi dừng
                    ),
                    "safetySettings", new Object[]{
                            Map.of(
                                    "category", "HARM_CATEGORY_HARASSMENT",
                                    "threshold", "BLOCK_MEDIUM_AND_ABOVE"
                            ),
                            Map.of(
                                    "category", "HARM_CATEGORY_HATE_SPEECH",
                                    "threshold", "BLOCK_MEDIUM_AND_ABOVE"
                            )
                    },
                    "systemInstruction", Map.of(
                            "parts", new Object[]{
                                    Map.of("text", """
                                                Bạn là một trợ lý ảo chuyên tư vấn về gạch ốp lát và thiết bị vệ sinh cho cửa hàng Hùng Hương.
                                                Chỉ trả lời các câu hỏi liên quan đến sản phẩm, giá cả, khuyến mãi hoặc tư vấn lựa chọn sản phẩm của cửa hàng.
                                                Nếu câu hỏi không liên quan đến gạch ốp lát, thiết bị vệ sinh hoặc các thông tin của cửa hàng, hãy lịch sự từ chối với nội dung:
                                                'Xin lỗi, tôi chỉ hỗ trợ các thông tin về gạch ốp lát và thiết bị vệ sinh của Hùng Hương.'
                                                Luôn trả lời ngắn gọn, rõ ràng, thân thiện và đúng chuyên môn.
                                            """)
                            }
                    )
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(fullUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);

            // Lấy text trả về
            Map<String, Object> candidate = ((List<Map<String, Object>>) result.get("candidates")).get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            Map<String, Object> part = ((List<Map<String, Object>>) content.get("parts")).get(0);
            return (String) part.get("text");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tìm sản phẩm liên quan dựa trên từ khóa trong câu hỏi
     */
    private List<Product> findRelevantProductInfo(String userMessage) {
        // Tách từ khóa đơn giản (có thể dùng NLP sau này)
        String[] keywords = userMessage.toLowerCase().split("\\s+");

        Set<Product> results = new HashSet<>();
        for (String keyword : keywords) {
            if (keyword.length() > 2) { // bỏ qua từ quá ngắn
                results.addAll(productRepository.findByNameContainingIgnoreCase(keyword));
            }
        }
        return new ArrayList<>(results);
    }

    /**
     * Tạo mô tả dạng text cho danh sách sản phẩm truyền vào
     */
    private String buildProductDescription(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return "Không tìm thấy sản phẩm liên quan.";
        }
        return products.stream()
                .map(p -> String.format("• %s - giá: %,.0fđ - danh mục: %s - thương hiệu: %s",
                        p.getName(), p.calculateFinalPrice(),
                        p.getCategory() != null ? p.getCategory().getName() : "Không rõ",
                        p.getBrand() != null ? p.getBrand() : "Không rõ"))
                .collect(Collectors.joining("\n"));
    }
}
