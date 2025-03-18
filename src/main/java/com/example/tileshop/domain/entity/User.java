package com.example.tileshop.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "UN_USER_USERNAME", columnNames = "username"),
                @UniqueConstraint(name = "UN_USER_EMAIL", columnNames = "email")
        })
public class User {

    @Id
    @UuidGenerator
    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "full-name")
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone-number", nullable = false)
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id",
            foreignKey = @ForeignKey(name = "FK_USER_ROLE_ID"),
            referencedColumnName = "role_id",
            nullable = false)
    private Role role;

}
