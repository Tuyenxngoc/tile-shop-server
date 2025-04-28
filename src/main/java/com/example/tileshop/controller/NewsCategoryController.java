package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.newscategory.NewsCategoryRequestDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.service.NewsCategoryService;
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
@Tag(name = "NewsCategory")
public class NewsCategoryController {
    NewsCategoryService newsCategoryService;

    @Operation(summary = "API Create News Category")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(UrlConstant.NewsCategory.CREATE)
    public ResponseEntity<?> createNewsCategory(@Valid @RequestBody NewsCategoryRequestDTO requestDTO) {
        return VsResponseUtil.success(HttpStatus.CREATED, newsCategoryService.save(requestDTO));
    }

    @Operation(summary = "API Update News Category")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(UrlConstant.NewsCategory.UPDATE)
    public ResponseEntity<?> updateNewsCategory(
            @PathVariable Long id,
            @Valid @RequestBody NewsCategoryRequestDTO requestDTO
    ) {
        return VsResponseUtil.success(newsCategoryService.update(id, requestDTO));
    }

    @Operation(summary = "API Delete News Category")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.NewsCategory.DELETE)
    public ResponseEntity<?> deleteNewsCategory(@PathVariable Long id) {
        return VsResponseUtil.success(newsCategoryService.delete(id));
    }

    @Operation(summary = "API Get News Categories")
    @GetMapping(UrlConstant.NewsCategory.GET_ALL)
    public ResponseEntity<?> getNewsCategories(@ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(newsCategoryService.findAll(requestDTO));
    }

    @Operation(summary = "API Get News Category By Id")
    @GetMapping(UrlConstant.NewsCategory.GET_BY_ID)
    public ResponseEntity<?> getNewsCategoryById(@PathVariable Long id) {
        return VsResponseUtil.success(newsCategoryService.findById(id));
    }
}