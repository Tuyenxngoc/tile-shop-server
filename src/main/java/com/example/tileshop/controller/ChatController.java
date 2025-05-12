package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.service.GeminiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Chat")
public class ChatController {
    private final GeminiService geminiService;

    @PostMapping("/chat")
    public String chatWithAI(@RequestParam String message) {
        try {
            return geminiService.askGemini(message);
        } catch (Exception e) {
            return "Có lỗi khi gọi Gemini API: " + e.getMessage();
        }
    }
}
