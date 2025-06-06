package com.example.tileshop.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.tileshop.exception.BadGatewayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadFileUtil {
    private final Cloudinary cloudinary;

    private static String getResourceType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return "image";
            } else if (contentType.startsWith("video/")) {
                return "video";
            } else {
                return "auto";
            }
        } else {
            throw new BadGatewayException("Invalid file!");
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            String resourceType = getResourceType(file);
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", resourceType));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new BadGatewayException("Upload file failed!");
        }
    }

    public String uploadImage(byte[] bytes) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(bytes, ObjectUtils.asMap("resource_type", "image"));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new BadGatewayException("Upload image failed!");
        }
    }

    public String copyImageFromUrl(String imageUrl) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(imageUrl, ObjectUtils.asMap("resource_type", "image"));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new BadGatewayException("Copy image from URL failed!");
        }
    }

    public void destroyFileWithUrl(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }

        int startIndex = url.lastIndexOf("/") + 1;
        int endIndex = url.lastIndexOf(".");
        if (startIndex <= 0 || endIndex <= startIndex) {
            return;
        }

        String publicId = url.substring(startIndex, endIndex);
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("invalidate", true));
            log.info("Destroy image public id {} {}", publicId, result.toString());
        } catch (IOException e) {
            throw new BadGatewayException("Remove file failed!");
        }
    }

    public boolean isImageInvalid(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        String contentType = file.getContentType();
        return contentType == null || !contentType.startsWith("image/");
    }

    public boolean isPdfInvalid(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        String contentType = file.getContentType();
        return contentType == null || !contentType.equalsIgnoreCase("application/pdf");
    }
}
