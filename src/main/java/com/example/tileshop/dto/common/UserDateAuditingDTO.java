package com.example.tileshop.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserDateAuditingDTO extends DateAuditingDTO {

    protected String createdBy;

    protected String lastModifiedBy;

}
