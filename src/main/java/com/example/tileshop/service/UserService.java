package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.user.CreateUserRequestDTO;
import com.example.tileshop.dto.user.UpdateUserRequestDTO;
import com.example.tileshop.dto.user.UserResponseDTO;

public interface UserService {
    void init();

    CommonResponseDTO save(CreateUserRequestDTO requestDTO);

    CommonResponseDTO update(String id, UpdateUserRequestDTO requestDTO);

    CommonResponseDTO delete(String id);

    PaginationResponseDTO<UserResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    UserResponseDTO findById(String id);

    CommonResponseDTO toggleActive(String id);
}
