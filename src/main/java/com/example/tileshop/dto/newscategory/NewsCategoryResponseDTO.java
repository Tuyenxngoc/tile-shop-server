package com.example.tileshop.dto.newscategory;

import com.example.tileshop.dto.common.DateAuditingDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsCategoryResponseDTO extends DateAuditingDTO {
    private long id;

    private String name;

    private String slug;
}
