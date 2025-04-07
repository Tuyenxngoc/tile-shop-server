package com.example.tileshop.dto.review;

import com.example.tileshop.dto.common.BaseEntityDTO;
import com.example.tileshop.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long id;

    private int rating;

    private String comment;

    private String imageUrl;

    private BaseEntityDTO customer;

    public ReviewResponseDTO(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.imageUrl = review.getImageUrl();
        this.customer = new BaseEntityDTO(review.getCustomer().getId(), review.getCustomer().getFullName());
    }
}
