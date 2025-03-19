package com.example.tileshop.dto.request.pagination;

import com.example.tileshop.constant.CommonConstant;
import com.example.tileshop.constant.SortByDataConstant;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationSortRequestDto extends PaginationRequestDto {

    @Parameter(description = "The name of property want to sort")
    private String sortBy = CommonConstant.EMPTY_STRING;

    @Parameter(description = "Sorting criteria - Default sort order is descending")
    private Boolean isAscending = Boolean.FALSE;

    public String getSortBy(SortByDataConstant constant) {
        return constant.getSortBy(sortBy);
    }

}
