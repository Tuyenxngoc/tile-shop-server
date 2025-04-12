package com.example.tileshop.entity;

import com.example.tileshop.entity.common.FlagDateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "attributes",
        uniqueConstraints = @UniqueConstraint(name = "UN_ATTRIBUTE_NAME", columnNames = "name"))
public class Attribute extends FlagDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attribute_id")
    private Long id;

    private String name;

    @Column(nullable = false)
    private boolean isRequired = false;

    @Column
    private String defaultValue;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CategoryAttribute> categoryAttributes = new ArrayList<>();

}
