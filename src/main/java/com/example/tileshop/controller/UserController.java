package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User")
public class UserController {

    UserService userService;

}
