package com.example.tileshop.dto.review;

import com.example.tileshop.constant.ErrorMessage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequestDTO {

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    @Min(value = 1, message = ErrorMessage.INVALID_MINIMUM_ONE)
    @Max(value = 5, message = ErrorMessage.INVALID_MAXIMUM_FIVE)
    private Integer rating;

    @Size(max = 500, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String comment;

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Long productId;

}