package com.example.tileshop.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    void init();

    List<String> uploadFiles(List<MultipartFile> files, String userId);
}
