package com.example.tileshop.controller;

import com.example.tileshop.annotation.CurrentUser;
import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationSortRequestDTO;
import com.example.tileshop.dto.review.CreateReviewRequestDTO;
import com.example.tileshop.security.CustomUserDetails;
import com.example.tileshop.service.ReviewService;
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

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Review")
public class ReviewController {

    ReviewService reviewService;

    @Operation(summary = "Get reviews by product ID")
    @GetMapping(UrlConstant.Review.GET_BY_PRODUCT_ID)
    public ResponseEntity<?> getReviewsByProductId(@PathVariable Long productId, PaginationSortRequestDTO requestDTO) {
        return VsResponseUtil.success(reviewService.getReviewsByProductId(productId, requestDTO));
    }

    @Operation(summary = "Get review summary by product ID")
    @GetMapping(UrlConstant.Review.GET_SUMMARY_BY_PRODUCT_ID)
    public ResponseEntity<?> getReviewSummary(@PathVariable Long productId) {
        return VsResponseUtil.success(reviewService.getReviewSummary(productId));
    }

    @Operation(summary = "Get pending reviews")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Review.GET_PENDING)
    public ResponseEntity<?> getPendingReviews(PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(reviewService.getPendingReviews(requestDTO));
    }

    @Operation(summary = "Create a new review")
    @PostMapping(value = UrlConstant.Review.CREATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addReview(
            @RequestPart("review") @Valid CreateReviewRequestDTO requestDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, reviewService.addReview(requestDTO, images, userDetails.getUserId()));
    }

    @Operation(summary = "Approve a review")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(UrlConstant.Review.APPROVE)
    public ResponseEntity<?> approveReview(@PathVariable Long id, @CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(reviewService.approveReview(id, userDetails.getUsername()));
    }

    @Operation(summary = "Reject a review")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(UrlConstant.Review.REJECT)
    public ResponseEntity<?> rejectReview(@PathVariable Long id) {
        return VsResponseUtil.success(reviewService.rejectReview(id));
    }

    @Operation(summary = "Delete a review")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.Review.DELETE)
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        return VsResponseUtil.success(reviewService.deleteReview(id));
    }

}