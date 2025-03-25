package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.category.CategoryRequestDto;
import com.example.tileshop.dto.request.pagination.PaginationFullRequestDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Category")
public class CategoryController {

    CategoryService categoryService;

    @Operation(summary = "API Create Category")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(UrlConstant.Category.CREATE)
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        return VsResponseUtil.success(HttpStatus.CREATED, categoryService.save(requestDto));
    }

    @Operation(summary = "API Update Category")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(UrlConstant.Category.UPDATE)
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDto requestDto
    ) {
        return VsResponseUtil.success(categoryService.update(id, requestDto));
    }

    @Operation(summary = "API Delete Category")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.Category.DELETE)
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        return VsResponseUtil.success(categoryService.delete(id));
    }

    @Operation(summary = "API Get Categories")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Category.GET_ALL)
    public ResponseEntity<?> getCategories(@ParameterObject PaginationFullRequestDto requestDto) {
        return VsResponseUtil.success(categoryService.findAll(requestDto));
    }

    @Operation(summary = "API Get Category By Id")
    @PreAuthorize("hasRole('ROLE_VIEW_CATEGORY')")
    @GetMapping(UrlConstant.Category.GET_BY_ID)
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return VsResponseUtil.success(categoryService.findById(id));
    }

}