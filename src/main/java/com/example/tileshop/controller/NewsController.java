package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.news.NewsRequestDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.service.NewsService;
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
@Tag(name = "News")
public class NewsController {

    NewsService newsService;

    @Operation(summary = "API Create News")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = UrlConstant.News.CREATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNews(
            @RequestPart("news") @Valid NewsRequestDTO requestDTO,
            @RequestPart(value = "image") MultipartFile image
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, newsService.save(requestDTO, image));
    }

    @Operation(summary = "API Update News")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = UrlConstant.News.UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateNews(
            @PathVariable Long id,
            @RequestPart("news") @Valid NewsRequestDTO requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return VsResponseUtil.success(newsService.update(id, requestDTO, image));
    }

    @Operation(summary = "API Delete News")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.News.DELETE)
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        return VsResponseUtil.success(newsService.delete(id));
    }

    @Operation(summary = "API Get News")
    @GetMapping(UrlConstant.News.GET_ALL)
    public ResponseEntity<?> getNews(
            @RequestParam(value = "excludeId", required = false) Long excludeId,
            @ParameterObject PaginationFullRequestDTO requestDTO
    ) {
        return VsResponseUtil.success(newsService.findAll(excludeId, requestDTO));
    }

    @Operation(summary = "API Get News By Id")
    @GetMapping(UrlConstant.News.GET_BY_ID)
    public ResponseEntity<?> getNewsById(@PathVariable Long id) {
        return VsResponseUtil.success(newsService.findById(id));
    }

    @Operation(summary = "API Get News By Slug")
    @GetMapping(UrlConstant.News.GET_BY_SLUG)
    public ResponseEntity<?> getNewsBySlug(@PathVariable String slug) {
        return VsResponseUtil.success(newsService.findBySlug(slug));
    }

}
