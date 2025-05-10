package com.example.tileshop.mapper;

import com.example.tileshop.dto.common.BaseEntityDTO;
import com.example.tileshop.dto.user.UserResponseDTO;
import com.example.tileshop.entity.User;

public class UserMapper {
    public static UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setFullName(user.getFullName());
        dto.setAddress(user.getAddress());
        dto.setGender(user.getGender());
        dto.setActiveFlag(user.getActiveFlag());
        dto.setRole(new BaseEntityDTO(user.getRole().getId(), user.getRole().getName()));

        return dto;
    }
}