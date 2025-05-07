package com.example.tileshop.dto.attribute;

import com.example.tileshop.dto.common.DateAuditingDTO;
import com.example.tileshop.entity.Attribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeResponseDTO extends DateAuditingDTO {
    private long id;

    private String name;

    private Boolean isRequired;

    private String defaultValue;

    public AttributeResponseDTO(Attribute attribute) {
        this.createdDate = attribute.getCreatedDate();
        this.lastModifiedDate = attribute.getLastModifiedDate();
        this.id = attribute.getId();
        this.name = attribute.getName();
        this.isRequired = attribute.isRequired();
        this.defaultValue = attribute.getDefaultValue();
    }
}
