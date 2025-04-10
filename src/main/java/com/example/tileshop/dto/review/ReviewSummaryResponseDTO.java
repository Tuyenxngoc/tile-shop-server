package com.example.tileshop.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ReviewSummaryResponseDTO {

    private double averageRating;

    private int totalReviews;

    private Map<Integer, RatingDetail> ratingBreakdown;

    @Data
    public static class RatingDetail {
        private int count;
        private double percentage;
    }

}
