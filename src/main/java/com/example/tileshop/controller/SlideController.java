package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.slide.SlideRequestDTO;
import com.example.tileshop.service.SlideService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Slide")
public class SlideController {
    SlideService slideService;

    @Operation(summary = "API Create Slide")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = UrlConstant.Slide.CREATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createSlide(
            @RequestPart("slide") @Valid SlideRequestDTO requestDTO,
            @RequestPart(value = "image") MultipartFile image
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, slideService.save(requestDTO, image));
    }

    @Operation(summary = "API Update Slide")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = UrlConstant.Slide.UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateSlide(
            @PathVariable String id,
            @RequestPart("slide") @Valid SlideRequestDTO requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return VsResponseUtil.success(slideService.update(id, requestDTO, image));
    }

    @Operation(summary = "API Delete Slide")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.Slide.DELETE)
    public ResponseEntity<?> deleteSlide(@PathVariable String id) {
        return VsResponseUtil.success(slideService.delete(id));
    }

    @Operation(summary = "API Get Slides")
    @GetMapping(UrlConstant.Slide.GET_ALL)
    public ResponseEntity<?> getSlides() {
        return VsResponseUtil.success(slideService.findAll());
    }

    @Operation(summary = "API Get Slide By Id")
    @GetMapping(UrlConstant.Slide.GET_BY_ID)
    public ResponseEntity<?> getSlideById(@PathVariable String id) {
        return VsResponseUtil.success(slideService.findById(id));
    }
}