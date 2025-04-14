package com.example.tileshop.dto.review;

import com.example.tileshop.dto.common.BaseEntityDTO;
import com.example.tileshop.dto.common.DateAuditingDTO;
import com.example.tileshop.entity.Review;
import com.example.tileshop.entity.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewForUserResponseDTO extends DateAuditingDTO {
    private long id;

    private int rating;

    private String comment;

    private List<String> images;

    private BaseEntityDTO user;

    public ReviewForUserResponseDTO(Review review) {
        this.createdDate = review.getCreatedDate();
        this.lastModifiedDate = review.getLastModifiedDate();
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.images = review.getImages().stream().map(ReviewImage::getImageUrl).toList();
        this.user = new BaseEntityDTO(review.getUser().getId(), review.getUser().getFullName());
    }
}
