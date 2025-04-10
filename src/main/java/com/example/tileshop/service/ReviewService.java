package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PaginationSortRequestDTO;
import com.example.tileshop.dto.review.CreateReviewRequestDTO;
import com.example.tileshop.dto.review.ReviewResponseDTO;
import com.example.tileshop.dto.review.ReviewSummaryResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    PaginationResponseDTO<ReviewResponseDTO> getReviewsByProductId(String productId, PaginationSortRequestDTO requestDTO);

    ReviewSummaryResponseDTO getReviewSummary(String productId);

    PaginationResponseDTO<ReviewResponseDTO> getPendingReviews(PaginationFullRequestDTO requestDTO);

    CommonResponseDTO addReview(CreateReviewRequestDTO requestDTO, List<MultipartFile> images);

    CommonResponseDTO approveReview(Long id, String username);

    CommonResponseDTO rejectReview(Long id);

    CommonResponseDTO deleteReview(Long id);
}