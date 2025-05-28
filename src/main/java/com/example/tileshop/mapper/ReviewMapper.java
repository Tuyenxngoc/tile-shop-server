package com.example.tileshop.mapper;

import com.example.tileshop.dto.review.ReviewResponseDTO;
import com.example.tileshop.entity.Review;
import com.example.tileshop.entity.ReviewImage;

public class ReviewMapper {
    public static ReviewResponseDTO toDTO(Review review) {
        if (review == null) {
            return null;
        }

        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setCreatedDate(review.getCreatedDate());
        dto.setLastModifiedDate(review.getLastModifiedDate());
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setStatus(review.getStatus());
        if (review.getImages() != null) {
            dto.setImages(review.getImages()
                    .stream()
                    .map(ReviewImage::getImageUrl)
                    .toList());
        }
        dto.setUser(UserMapper.toDTO(review.getUser()));
        dto.setProduct(ProductMapper.toResponseDTO(review.getProduct()));

        return dto;
    }
}