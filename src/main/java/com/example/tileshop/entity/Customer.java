package com.example.tileshop.entity;

import com.example.tileshop.constant.Gender;
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
@Table(name = "customers",
        uniqueConstraints = @UniqueConstraint(name = "UK_CUSTOMER_USER_ID", columnNames = "user_id")
)
public class Customer extends FlagDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_CUSTOMER_USER_ID"), nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

}
