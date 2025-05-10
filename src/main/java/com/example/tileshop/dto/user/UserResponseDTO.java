package com.example.tileshop.dto.user;

import com.example.tileshop.constant.Gender;
import com.example.tileshop.dto.common.BaseEntityDTO;
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
}
