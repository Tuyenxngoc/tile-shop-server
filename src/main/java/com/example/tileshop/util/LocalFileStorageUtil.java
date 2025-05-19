package com.example.tileshop.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class LocalFileStorageUtil {
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Lưu file vào local và trả về đường dẫn tuyệt đối
     */
    public String saveFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.createDirectories(filePath.getParent()); // Tạo thư mục nếu chưa tồn tại
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file: " + fileName, e);
        }

        return filePath.toString();
    }

    /**
     * Xóa file khỏi local
     */
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Không thể xóa file: " + filePath, e);
        }
    }

    /**
     * Kiểm tra file có tồn tại không
     */
    public boolean isFileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }
}
