package com.example.tileshop.service.impl;

import com.example.tileshop.constant.CommonConstant;
import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.RoleConstant;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.user.UserRequestDTO;
import com.example.tileshop.dto.user.UserResponseDTO;
import com.example.tileshop.entity.User;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.RoleRepository;
import com.example.tileshop.repository.UserRepository;
import com.example.tileshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private User getEntity(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, id));
    }

    public void validatePassword(String password) {
        if (!password.matches(CommonConstant.REGEXP_PASSWORD)) {
            throw new BadRequestException(ErrorMessage.INVALID_FORMAT_PASSWORD);
        }
    }

    @Override
    public void init() {
        log.info("Starting user initialization");

        if (userRepository.count() != 0) {
            log.info("Users already exist. Skipping initialization.");
            return;
        }

        User user = new User();
        user.setUsername("tuyenngoc");
        user.setEmail("tuyenngoc@gmail.com");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setRole(roleRepository.findByCode(RoleConstant.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Invalid role")));

        userRepository.save(user);
        log.info("Initial admin user created successfully with username: {}", user.getUsername());
    }

    @Override
    public CommonResponseDTO save(UserRequestDTO requestDTO) {
        return null;
    }

    @Override
    public CommonResponseDTO update(String id, UserRequestDTO requestDTO) {
        User user = getEntity(id);
        return null;
    }

    @Override
    public CommonResponseDTO delete(String id) {
        User user = getEntity(id);
        return null;
    }

    @Override
    public PaginationResponseDTO<UserResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        return null;
    }

    @Override
    public UserResponseDTO findById(String id) {
        User user = getEntity(id);
        return null;
    }

}
