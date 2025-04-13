package com.example.tileshop.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public abstract class ActiveAuditing extends DateAuditing {

    @Column(name = "active_flag", nullable = false)
    protected Boolean activeFlag = Boolean.TRUE;

}
