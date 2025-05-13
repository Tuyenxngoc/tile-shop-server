package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.chat.ChatRequestDTO;
import com.example.tileshop.service.GeminiService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Chat")
public class ChatController {
    private final GeminiService geminiService;

    @Operation(summary = "API Chat with AI Gemini")
    @PostMapping(UrlConstant.Chat.CHAT_WITH_AI)
    public ResponseEntity<?> chatWithAI(@Valid @RequestBody ChatRequestDTO requestDTO) {
        return VsResponseUtil.success(geminiService.askGemini(requestDTO));
    }
}
