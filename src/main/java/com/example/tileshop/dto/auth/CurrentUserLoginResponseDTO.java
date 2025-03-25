package com.example.tileshop.dto.auth;

import com.example.tileshop.constant.RoleConstant;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class CurrentUserLoginResponseDTO {

    private String userId;

    private String username;

    private Set<RoleConstant> roleNames;

}
