package com.example.tileshop.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class UserDateAuditing extends DateAuditing {
    @CreatedBy
    @Column(updatable = false, nullable = false)
    protected String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    protected String lastModifiedBy;
}
