package com.example.tileshop.dto.filter;

import com.example.tileshop.constant.ReviewStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewFilterDTO {
    private Integer rating;        
    
    private Boolean hasImage;      
    
    private Boolean hasContent;    
    
    private ReviewStatus status;      
}
