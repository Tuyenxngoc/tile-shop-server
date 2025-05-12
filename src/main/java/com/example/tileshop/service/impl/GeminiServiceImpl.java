package com.example.tileshop.service.impl;

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
    public String askGemini(String userMessage) throws Exception {
        String fullUrl = apiUrl + "?key=" + apiKey;

        String requestBody = objectMapper.writeValueAsString(Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", userMessage)
                        })
                }
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
    }

}
