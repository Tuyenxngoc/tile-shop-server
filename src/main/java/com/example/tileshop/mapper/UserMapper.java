package com.example.tileshop.mapper;

import com.example.tileshop.dto.user.UserResponseDTO;
import com.example.tileshop.entity.User;

public class UserMapper {
    public static UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        // TODO: set fields từ User vào dto
        // vd: dto.setId(user.getId());

        return dto;
    }
}