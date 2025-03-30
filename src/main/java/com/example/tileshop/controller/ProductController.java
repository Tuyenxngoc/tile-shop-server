package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.product.ProductRequestDTO;
import com.example.tileshop.service.ProductService;
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

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Product")
public class ProductController {
    ProductService productService;

    @Operation(summary = "API Create Product")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = UrlConstant.Product.CREATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("product") @Valid ProductRequestDTO requestDTO,
            @RequestPart(value = "images") List<MultipartFile> images
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, productService.save(requestDTO, images));
    }

    @Operation(summary = "API Update Product")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = UrlConstant.Product.UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductRequestDTO requestDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return VsResponseUtil.success(productService.update(id, requestDTO, images));
    }

    @Operation(summary = "API Delete Product")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.Product.DELETE)
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return VsResponseUtil.success(productService.delete(id));
    }

    @Operation(summary = "API Get Products")
    @GetMapping(UrlConstant.Product.GET_ALL)
    public ResponseEntity<?> getProducts(@ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(productService.findAll(requestDTO));
    }

    @Operation(summary = "API Get Product By Id")
    @GetMapping(UrlConstant.Product.GET_BY_ID)
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return VsResponseUtil.success(productService.findById(id));
    }

    @Operation(summary = "API Get Product By Slug")
    @GetMapping(UrlConstant.Product.GET_BY_SLUG)
    public ResponseEntity<?> getProductBySlug(@PathVariable String slug) {
        return VsResponseUtil.success(productService.findBySlug(slug));
    }
}