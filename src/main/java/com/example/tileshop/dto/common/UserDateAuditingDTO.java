package com.example.tileshop.dto.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class UserDateAuditingDTO extends DateAuditingDTO {

    protected String createdBy;

    protected String lastModifiedBy;

}
