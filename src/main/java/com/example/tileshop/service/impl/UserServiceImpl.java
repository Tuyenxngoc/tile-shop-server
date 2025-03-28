package com.example.tileshop.service.impl;

import com.example.tileshop.constant.CommonConstant;
import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.RoleConstant;
import com.example.tileshop.entity.User;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.RoleRepository;
import com.example.tileshop.repository.UserRepository;
import com.example.tileshop.service.UserService;
import com.example.tileshop.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final UploadFileUtil uploadFileUtil;

    private User getEntity(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, id));
    }

    public void validatePassword(String password) {
        if (!password.matches(CommonConstant.REGEXP_PASSWORD)) {
            throw new BadRequestException(ErrorMessage.INVALID_FORMAT_PASSWORD);
        }
    }

    @Override
    public void init() {
        log.info("Starting user initialization");

        if (userRepository.count() != 0) {
            log.info("Users already exist. Skipping initialization.");
            return;
        }

        User user = new User();
        user.setUsername("tuyenngoc");
        user.setEmail("tuyenngoc@gmail.com");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setRole(roleRepository.findByCode(RoleConstant.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Invalid role")));

        userRepository.save(user);
        log.info("Initial admin user created successfully with username: {}", user.getUsername());
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException(ErrorMessage.INVALID_FILE_REQUIRED);
        }

        List<String> allowedMimeTypes = Arrays.asList(
                "image/jpeg", "image/png", "image/gif",
                "application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );

        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<CompletableFuture<String>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> uploadSingleFile(file, allowedMimeTypes), executor))
                .toList();

        List<String> uploadedFiles = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        executor.shutdown();

        return uploadedFiles;
    }

    private String uploadSingleFile(MultipartFile file, List<String> allowedMimeTypes) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !allowedMimeTypes.contains(contentType)) {
                throw new BadRequestException(ErrorMessage.INVALID_FILE_TYPE);
            }

            return uploadFileUtil.uploadFile(file);
        } catch (Exception e) {
            log.error("Error while uploading file: {} - {}", file.getOriginalFilename(), e.getMessage());
            return null;
        }
    }

}
