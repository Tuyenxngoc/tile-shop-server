package com.example.tileshop.service;

import com.example.tileshop.dto.chat.ChatRequestDTO;

public interface GeminiService {
    String askGemini(ChatRequestDTO requestDTO);
}
