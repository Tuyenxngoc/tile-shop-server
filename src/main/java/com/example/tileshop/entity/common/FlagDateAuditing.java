package com.example.tileshop.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public abstract class FlagDateAuditing extends DateAuditing {
    @Column(nullable = false)
    protected Boolean deleteFlag = Boolean.FALSE;

    @Column(nullable = false)
    protected Boolean activeFlag = Boolean.TRUE;
}