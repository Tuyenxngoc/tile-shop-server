package com.example.tileshop.dto.review;

import com.example.tileshop.constant.ReviewStatus;
import com.example.tileshop.dto.common.BaseEntityDTO;
import com.example.tileshop.dto.common.DateAuditingDTO;
import com.example.tileshop.entity.Review;
import com.example.tileshop.entity.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewResponseDTO extends DateAuditingDTO {//user
    private Long id;

    private int rating;

    private String comment;
    
    private ReviewStatus status;//todo

    private List<String> images;

    private BaseEntityDTO customer;

    public ReviewResponseDTO(Review review) {
        this.createdDate = review.getCreatedDate();
        this.lastModifiedDate = review.getLastModifiedDate();
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.status = review.getStatus();
        this.images = review.getImages().stream().map(ReviewImage::getImageUrl).toList();
        this.customer = new BaseEntityDTO(review.getCustomer().getId(), review.getCustomer().getFullName());
    }
}
