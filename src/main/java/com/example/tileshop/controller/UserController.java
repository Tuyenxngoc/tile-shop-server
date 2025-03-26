package com.example.tileshop.controller;

import com.example.tileshop.annotation.CurrentUser;
import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.security.CustomUserDetails;
import com.example.tileshop.service.UserService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User")
public class UserController {

    UserService userService;

    @Operation(summary = "API upload files")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = UrlConstant.User.UPLOAD, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(userService.uploadFiles(files, userDetails.getUserId()));
    }

}
