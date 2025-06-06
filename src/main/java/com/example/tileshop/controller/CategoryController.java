package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.category.CategoryRequestDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.service.CategoryService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Category")
public class CategoryController {
    CategoryService categoryService;

    @Operation(summary = "API Create Category")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = UrlConstant.Category.CREATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCategory(
            @RequestPart("category") @Valid CategoryRequestDTO requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, categoryService.save(requestDTO, image));
    }

    @Operation(summary = "API Update Category")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = UrlConstant.Category.UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestPart("category") @Valid CategoryRequestDTO requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return VsResponseUtil.success(categoryService.update(id, requestDTO, image));
    }

    @Operation(summary = "API Delete Category")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.Category.DELETE)
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        return VsResponseUtil.success(categoryService.delete(id));
    }

    @Operation(summary = "API Get Categories")
    @GetMapping(UrlConstant.Category.GET_ALL)
    public ResponseEntity<?> getCategories(@ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(categoryService.findAll(requestDTO));
    }

    @Operation(summary = "API Get Category By Id")
    @GetMapping(UrlConstant.Category.GET_BY_ID)
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return VsResponseUtil.success(categoryService.findById(id));
    }

    @Operation(summary = "API Get Category By Slug")
    @GetMapping(UrlConstant.Category.GET_BY_SLUG)
    public ResponseEntity<?> getCategoryBySlug(@PathVariable String slug) {
        return VsResponseUtil.success(categoryService.findBySlug(slug));
    }

    @Operation(summary = "API Get Categories Tree")
    @GetMapping(UrlConstant.Category.GET_TREE)
    public ResponseEntity<?> getCategoriesTree() {
        return VsResponseUtil.success(categoryService.getCategoriesTree());
    }
}