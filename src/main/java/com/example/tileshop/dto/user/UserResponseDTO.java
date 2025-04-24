package com.example.tileshop.dto.user;

import com.example.tileshop.constant.Gender;
import com.example.tileshop.dto.common.BaseEntityDTO;
import com.example.tileshop.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String id;

    private String username;

    private String email;

    private String phoneNumber;

    private String fullName;

    private String address;

    private Gender gender;

    private Boolean activeFlag;

    private BaseEntityDTO role;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.fullName = user.getFullName();
        this.address = user.getAddress();
        this.gender = user.getGender();
        this.activeFlag = user.getActiveFlag();
        this.role = new BaseEntityDTO(user.getRole().getId(), user.getRole().getName());
    }
}
