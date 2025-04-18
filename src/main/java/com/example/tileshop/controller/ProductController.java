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
import java.util.Set;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Product")
public class ProductController {

    ProductService productService;

    // -------------------- ADMIN APIs --------------------

    @Operation(summary = "API Create Product")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = UrlConstant.Product.Admin.CREATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("product") @Valid ProductRequestDTO requestDTO,
            @RequestPart(value = "images") List<MultipartFile> images
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, productService.save(requestDTO, images));
    }

    @Operation(summary = "API Update Product")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = UrlConstant.Product.Admin.UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductRequestDTO requestDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "existingImages", required = false) Set<String> existingImageUrls
    ) {
        return VsResponseUtil.success(productService.update(id, requestDTO, images, existingImageUrls));
    }

    @Operation(summary = "API Delete Product")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.Product.Admin.DELETE)
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return VsResponseUtil.success(productService.delete(id));
    }

    @Operation(summary = "API Get Products")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Product.Admin.GET_ALL)
    public ResponseEntity<?> getProducts(@ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(productService.findAll(requestDTO));
    }

    @Operation(summary = "API Get Product By Id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Product.Admin.GET_BY_ID)
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return VsResponseUtil.success(productService.findById(id));
    }

    // -------------------- USER APIs --------------------

    @Operation(summary = "User - API Get Products")
    @GetMapping(UrlConstant.Product.User.GET_ALL)
    public ResponseEntity<?> getAllProductsForUser(@ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(productService.userFindAll(requestDTO));
    }

    @Operation(summary = "User - API Get Product By Id")
    @GetMapping(UrlConstant.Product.User.GET_BY_ID)
    public ResponseEntity<?> getProductDetailsById(@PathVariable Long id) {
        return VsResponseUtil.success(productService.userFindById(id));
    }

    @Operation(summary = "User - API Get Product By Slug")
    @GetMapping(UrlConstant.Product.User.GET_BY_SLUG)
    public ResponseEntity<?> getProductDetailsBySlug(@PathVariable String slug) {
        return VsResponseUtil.success(productService.userFindBySlug(slug));
    }
}