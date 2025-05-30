package com.example.tileshop.service.impl;

import com.example.tileshop.dto.chat.ChatMessageDTO;
import com.example.tileshop.dto.chat.ChatRequestDTO;
import com.example.tileshop.entity.Product;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.service.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
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
            String introText;
            if (matchedProducts.isEmpty()) {
                matchedProducts = productRepository.findTopSellingProductsOnlyEntities(null, null, Pageable.ofSize(5));
                introText = "Không tìm thấy sản phẩm khớp. Dưới đây là các sản phẩm bán chạy:\n";
            } else {
                introText = "Dưới đây là các sản phẩm liên quan đến câu hỏi của khách:\n";
            }

            // 2. Tạo phần mô tả sản phẩm
            String productInfo = buildProductDescription(introText, matchedProducts);

            // 3. Tạo request body gửi Gemini
            List<Map<String, Object>> partsList = new ArrayList<>();

            // Lịch sử hội thoại
            for (ChatMessageDTO msg : requestDTO.getConversation()) {
                partsList.add(Map.of("role", msg.getRole(), "parts", List.of(Map.of("text", msg.getContent()))));
            }

            // Câu hỏi hiện tại
            partsList.add(Map.of("role", "user", "parts", List.of(
                    Map.of("text", "Dữ liệu sản phẩm của cửa hàng:\n" + productInfo),
                    Map.of("text", "Câu hỏi khách hàng: " + userMessage)
            )));
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "contents", partsList,
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
                                                Bạn là một trợ lý ảo của cửa hàng Hùng Hương – chuyên cung cấp gạch ốp lát và thiết bị vệ sinh.
                                                Mục tiêu của bạn là hỗ trợ khách hàng trực tuyến thông qua website chính thức của cửa hàng.
                                            
                                                Những điều cần ghi nhớ:
                                            
                                                1. **Phạm vi hỗ trợ**:
                                                   - Chỉ trả lời các câu hỏi liên quan đến sản phẩm (gạch, thiết bị vệ sinh), giá bán, khuyến mãi, tình trạng còn hàng, hướng dẫn sử dụng, bảo hành, thương hiệu, và tư vấn lựa chọn sản phẩm.
                                                   - Không trả lời các câu hỏi không liên quan (chính trị, giải trí, tôn giáo, v.v.). Nếu gặp câu hỏi ngoài phạm vi, hãy lịch sự từ chối với nội dung:
                                                     ❝Xin lỗi, tôi chỉ hỗ trợ các thông tin về gạch ốp lát và thiết bị vệ sinh của Hùng Hương.❞
                                            
                                                2. **Bối cảnh trò chuyện**:
                                                   - Khách hàng đang tương tác trực tiếp trên website chính thức của Hùng Hương.
                                                   - Website có hỗ trợ xem thông tin và mua hàng trực tuyến.
                                                   - Khi được hỏi, hãy xác nhận rằng khách có thể mua hàng hoặc xem chi tiết qua trang web.
                                            
                                                3. **Văn phong & hành vi**:
                                                   - Giao tiếp thân thiện, rõ ràng, ngắn gọn và đúng chuyên môn.
                                                   - Tránh trả lời chung chung hoặc vòng vo.
                                                   - Không được bịa thông tin nếu không chắc.
                                            
                                                4. **Sử dụng dữ liệu sản phẩm**:
                                                   - Nếu được cung cấp danh sách sản phẩm trong câu hỏi, hãy ưu tiên sử dụng dữ liệu đó để trả lời.
                                                   - Có thể chèn link sản phẩm nếu có sẵn.
                                            
                                                5. **Gợi ý thông minh**:
                                                   - Nếu khách hỏi một sản phẩm không tồn tại, có thể đề xuất các sản phẩm bán chạy khác có liên quan.
                                                   - Luôn cố gắng giữ cuộc trò chuyện tiếp tục bằng cách đưa ra câu hỏi gợi mở hoặc đề xuất hữu ích.
                                            
                                                Nhớ rằng bạn là một trợ lý ảo đáng tin cậy, hiểu rõ sản phẩm, và luôn sẵn sàng giúp khách hàng đưa ra lựa chọn tốt nhất.
                                            """)
                            }
                    )
            ));

            log.info("Request body gửi Gemini:\n{}", requestBody);

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
    private String buildProductDescription(String introText, List<Product> products) {
        if (products == null || products.isEmpty()) {
            return "Không tìm thấy sản phẩm liên quan.";
        }

        return introText + products.stream()
                .map(p -> String.format(
                        "• %s - giá: %,.0fđ - danh mục: %s - thương hiệu: %s\n  Link: http://localhost:3000/san-pham/%s",
                        p.getName(), p.calculateFinalPrice(),
                        p.getCategory() != null ? p.getCategory().getName() : "Không rõ",
                        p.getBrand() != null ? p.getBrand().getName() : "Không rõ",
                        p.getSlug()))
                .collect(Collectors.joining("\n"));
    }
}
