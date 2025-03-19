package com.example.tileshop.domain.mapper;

import com.example.tileshop.domain.dto.request.auth.RegisterRequestDto;
import com.example.tileshop.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequestDto requestDto);
}