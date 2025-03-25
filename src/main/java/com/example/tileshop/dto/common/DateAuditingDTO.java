package com.example.tileshop.dto.common;

import com.example.tileshop.constant.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public abstract class DateAuditingDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstant.PATTERN_DATE_TIME)
    protected LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstant.PATTERN_DATE_TIME)
    protected LocalDateTime lastModifiedDate;

}
