package com.example.tileshop.service.impl;

import com.example.tileshop.dto.chat.ChatRequestDTO;
import com.example.tileshop.service.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    @Override
    public String askGemini(ChatRequestDTO requestDTO) {
        String fullUrl = apiUrl + "?key=" + apiKey;

        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "contents", new Object[]{
                            Map.of(
                                    "role", "user",  // Có thể là "user" hoặc "model"
                                    "parts", new Object[]{
                                            Map.of("text", requestDTO.getMessage())
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
}
