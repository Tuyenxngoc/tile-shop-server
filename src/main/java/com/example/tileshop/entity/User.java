package com.example.tileshop.entity;

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

    @Column(name = "email", nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_USER_ROLE_ID"), nullable = false)
    @JsonIgnore
    private Role role;

}
