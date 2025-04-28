package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.brand.BrandRequestDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.service.BrandService;
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
@Tag(name = "Brand")
public class BrandController {
    BrandService brandService;

    // -------------------- ADMIN APIs --------------------

    @Operation(summary = "API Create Brand")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = UrlConstant.Brand.Admin.CREATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBrand(
            @RequestPart("brand") @Valid BrandRequestDTO requestDTO,
            @RequestPart(value = "image") MultipartFile image
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, brandService.save(requestDTO, image));
    }

    @Operation(summary = "API Update Brand")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = UrlConstant.Brand.Admin.UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBrand(
            @PathVariable Long id,
            @RequestPart("brand") @Valid BrandRequestDTO requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return VsResponseUtil.success(brandService.update(id, requestDTO, image));
    }

    @Operation(summary = "API Delete Brand")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.Brand.Admin.DELETE)
    public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
        return VsResponseUtil.success(brandService.delete(id));
    }

    @Operation(summary = "API Get Brands")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Brand.Admin.GET_ALL)
    public ResponseEntity<?> getBrands(@ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(brandService.findAll(requestDTO));
    }

    @Operation(summary = "API Get Brand By Id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Brand.Admin.GET_BY_ID)
    public ResponseEntity<?> getBrandById(@PathVariable Long id) {
        return VsResponseUtil.success(brandService.findById(id));
    }

    // -------------------- USER APIs --------------------

    @Operation(summary = "User - API Get All Brands")
    @GetMapping(UrlConstant.Brand.User.GET_ALL)
    public ResponseEntity<?> getAllBrandsForUser(@ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(brandService.userFindAll(requestDTO));
    }

    @Operation(summary = "User - API Get Brand By Id")
    @GetMapping(UrlConstant.Brand.User.GET_BY_ID)
    public ResponseEntity<?> getBrandDetailsById(@PathVariable Long id) {
        return VsResponseUtil.success(brandService.userFindById(id));
    }
}