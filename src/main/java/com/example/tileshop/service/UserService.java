package com.example.tileshop.service;

import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.user.UserRequestDTO;
import com.example.tileshop.dto.user.UserResponseDTO;

public interface UserService {
    void init();

    CommonResponseDTO save(UserRequestDTO requestDTO);

    CommonResponseDTO update(String id, UserRequestDTO requestDTO);

    CommonResponseDTO delete(String id);

    PaginationResponseDTO<UserResponseDTO> findAll(PaginationFullRequestDTO requestDTO);

    UserResponseDTO findById(String id);
}
