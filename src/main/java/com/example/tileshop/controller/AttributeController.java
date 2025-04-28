package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.attribute.AttributeRequestDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.service.AttributeService;
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
@Tag(name = "Attribute")
public class AttributeController {
    AttributeService attributeService;

    @Operation(summary = "API Create Attribute")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(UrlConstant.Attribute.CREATE)
    public ResponseEntity<?> createAttribute(@Valid @RequestBody AttributeRequestDTO requestDTO) {
        return VsResponseUtil.success(HttpStatus.CREATED, attributeService.save(requestDTO));
    }

    @Operation(summary = "API Update Attribute")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(UrlConstant.Attribute.UPDATE)
    public ResponseEntity<?> updateAttribute(
            @PathVariable Long id,
            @Valid @RequestBody AttributeRequestDTO requestDTO
    ) {
        return VsResponseUtil.success(attributeService.update(id, requestDTO));
    }

    @Operation(summary = "API Delete Attribute")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.Attribute.DELETE)
    public ResponseEntity<?> deleteAttribute(@PathVariable Long id) {
        return VsResponseUtil.success(attributeService.delete(id));
    }

    @Operation(summary = "API Get Attributes")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Attribute.GET_ALL)
    public ResponseEntity<?> getAttributes(@ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(attributeService.findAll(requestDTO));
    }

    @Operation(summary = "API Get Attribute By Id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Attribute.GET_BY_ID)
    public ResponseEntity<?> getAttributeById(@PathVariable Long id) {
        return VsResponseUtil.success(attributeService.findById(id));
    }

    @Operation(summary = "API Get Attributes By Category ID")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Attribute.GET_BY_CATEGORY_ID)
    public ResponseEntity<?> getAttributesByCategoryId(@PathVariable Long categoryId) {
        return VsResponseUtil.success(attributeService.findByCategoryId(categoryId));
    }
}