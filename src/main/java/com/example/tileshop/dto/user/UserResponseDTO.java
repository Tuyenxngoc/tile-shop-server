package com.example.tileshop.dto.user;

import com.example.tileshop.dto.common.BaseEntityDTO;
import com.example.tileshop.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private String id;

    private String username;

    private String password;

    private String email;

    private BaseEntityDTO role;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.role = new BaseEntityDTO(user.getRole().getId(), user.getRole().getName());
    }
}
