package com.example.tileshop.mapper;

import com.example.tileshop.dto.review.ReviewResponseDTO;
import com.example.tileshop.entity.Review;

public class ReviewMapper {

    public static ReviewResponseDTO toDTO(Review review) {
        if (review == null) {
            return null;
        }

        ReviewResponseDTO dto = new ReviewResponseDTO();
        // TODO: set fields từ Review vào dto
        // vd: dto.setId(review.getId());

        return dto;
    }

}