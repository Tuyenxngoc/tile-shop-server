package com.example.tileshop.dto.request.pagination;

import com.example.tileshop.constant.CommonConstant;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationSearchRequestDto extends PaginationRequestDto {

    @Parameter(description = "Keyword to search")
    private String keyword = CommonConstant.EMPTY_STRING;

    @Parameter(description = "Search by")
    private String searchBy = CommonConstant.EMPTY_STRING;

    public String getKeyword() {
        return keyword.trim();
    }

    public String getSearchBy() {
        return searchBy.trim();
    }

}
