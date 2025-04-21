package com.example.tileshop.dto.auth;

import com.example.tileshop.constant.Gender;
import com.example.tileshop.constant.RoleConstant;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class CurrentUserLoginResponseDTO {

    private String userId;

    private String username;

    private String email;

    private String phoneNumber;

    private String fullName;

    private String address;

    private Gender gender;

    private Set<RoleConstant> roleNames;

}
