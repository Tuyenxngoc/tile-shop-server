package com.example.tileshop.mapper;

import com.example.tileshop.dto.request.auth.RegisterRequestDto;
import com.example.tileshop.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequestDto requestDto);
}