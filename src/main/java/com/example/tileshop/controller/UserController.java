package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.user.CreateUserRequestDTO;
import com.example.tileshop.dto.user.UpdateUserRequestDTO;
import com.example.tileshop.service.UserService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User")
public class UserController {

    UserService userService;

    @Operation(summary = "API Create User")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(UrlConstant.User.CREATE)
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequestDTO requestDTO) {
        return VsResponseUtil.success(HttpStatus.CREATED, userService.save(requestDTO));
    }

    @Operation(summary = "API Update User")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(UrlConstant.User.UPDATE)
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody @Valid UpdateUserRequestDTO requestDTO) {
        return VsResponseUtil.success(userService.update(id, requestDTO));
    }

    @Operation(summary = "API Delete User")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(UrlConstant.User.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        return VsResponseUtil.success(userService.delete(id));
    }

    @Operation(summary = "API Get All Users")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.User.GET_ALL)
    public ResponseEntity<?> getAllUsers(@ParameterObject PaginationFullRequestDTO requestDTO) {
        return VsResponseUtil.success(userService.findAll(requestDTO));
    }

    @Operation(summary = "API Get User By Id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.User.GET_BY_ID)
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        return VsResponseUtil.success(userService.findById(id));
    }

    @Operation(summary = "API Toggle Active Flag")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(UrlConstant.User.TOGGLE_ACTIVE)
    public ResponseEntity<?> toggleActive(@PathVariable String id) {
        return VsResponseUtil.success(userService.toggleActive(id));
    }

}
