package com.example.tileshop.dto.review;

import com.example.tileshop.constant.ReviewStatus;
import com.example.tileshop.dto.common.DateAuditingDTO;
import com.example.tileshop.dto.product.ProductResponseDTO;
import com.example.tileshop.dto.user.UserResponseDTO;
import com.example.tileshop.entity.Review;
import com.example.tileshop.entity.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO extends DateAuditingDTO {
    private long id;

    private int rating;

    private String comment;

    private ReviewStatus status;

    private List<String> images;

    private UserResponseDTO user;

    private ProductResponseDTO product;

    public ReviewResponseDTO(Review review) {
        this.createdDate = review.getCreatedDate();
        this.lastModifiedDate = review.getLastModifiedDate();
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.status = review.getStatus();
        this.images = review.getImages().stream().map(ReviewImage::getImageUrl).toList();
        this.user = new UserResponseDTO(review.getUser());
        this.product = new ProductResponseDTO(review.getProduct());
    }
}
