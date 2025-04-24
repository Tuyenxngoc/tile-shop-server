package com.example.tileshop.dto.filter;

import com.example.tileshop.constant.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFilterDTO {
    private Integer rating;

    private Boolean hasImage;

    private Boolean hasContent;

    private ReviewStatus status;
}
