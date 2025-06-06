package com.example.tileshop.service.impl;

import com.example.tileshop.constant.*;
import com.example.tileshop.dto.auth.CurrentUserLoginResponseDTO;
import com.example.tileshop.dto.common.CommonResponseDTO;
import com.example.tileshop.dto.pagination.PaginationFullRequestDTO;
import com.example.tileshop.dto.pagination.PaginationResponseDTO;
import com.example.tileshop.dto.pagination.PagingMeta;
import com.example.tileshop.dto.user.CreateUserRequestDTO;
import com.example.tileshop.dto.user.UpdateProfileRequestDTO;
import com.example.tileshop.dto.user.UpdateUserRequestDTO;
import com.example.tileshop.dto.user.UserResponseDTO;
import com.example.tileshop.entity.Role;
import com.example.tileshop.entity.User;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.ConflictException;
import com.example.tileshop.exception.ForbiddenException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.mapper.UserMapper;
import com.example.tileshop.repository.RoleRepository;
import com.example.tileshop.repository.UserRepository;
import com.example.tileshop.service.UserService;
import com.example.tileshop.specification.UserSpecification;
import com.example.tileshop.util.MessageUtil;
import com.example.tileshop.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final MessageUtil messageUtil;

    private User getEntity(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, id));
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
        user.setPhoneNumber("0123456789");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setRole(roleRepository.findByCode(RoleConstant.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Invalid role")));

        userRepository.save(user);
        log.info("Initial admin user created successfully with username: {}", user.getUsername());
    }

    @Override
    public CommonResponseDTO save(CreateUserRequestDTO requestDTO) {
        if (!requestDTO.getPassword().equals(requestDTO.getRepeatPassword())) {
            throw new BadRequestException(ErrorMessage.INVALID_REPEAT_PASSWORD);
        }
        boolean isUsernameExists = userRepository.existsByUsername(requestDTO.getUsername());
        if (isUsernameExists) {
            throw new ConflictException(ErrorMessage.Auth.ERR_DUPLICATE_USERNAME);
        }
        boolean isEmailExists = userRepository.existsByEmail(requestDTO.getEmail());
        if (isEmailExists) {
            throw new ConflictException(ErrorMessage.Auth.ERR_DUPLICATE_EMAIL);
        }

        Role role = roleRepository.findById(requestDTO.getRoleId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Role.ERR_NOT_FOUND_ID, requestDTO.getRoleId()));

        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setEmail(requestDTO.getEmail());
        user.setFullName(requestDTO.getFullName());
        user.setPhoneNumber(requestDTO.getPhoneNumber());
        user.setAddress(requestDTO.getAddress());
        user.setGender(requestDTO.getGender() == null ? Gender.OTHER : requestDTO.getGender());
        user.setRole(role);

        userRepository.save(user);

        String message = messageUtil.getMessage(SuccessMessage.CREATE);
        return new CommonResponseDTO(message, UserMapper.toDTO(user));
    }

    @Override
    public CommonResponseDTO update(String id, UpdateUserRequestDTO requestDTO) {
        User user = getEntity(id);

        // Kiểm tra email trùng lặp
        if (!requestDTO.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ConflictException(ErrorMessage.Auth.ERR_DUPLICATE_EMAIL);
        }

        // Lấy ra quyền dựa thep role id
        Role role = roleRepository.findById(requestDTO.getRoleId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Role.ERR_NOT_FOUND_ID, requestDTO.getRoleId()));

        // Nếu người dùng hiện tại là admin, không cho phép thay đổi quyền thành role khác ngoài admin
        if (RoleConstant.ROLE_ADMIN.equals(user.getRole().getCode()) && !RoleConstant.ROLE_ADMIN.equals(role.getCode())) {
            throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN_UPDATE_DELETE);
        }

        user.setFullName(requestDTO.getFullName());
        user.setEmail(requestDTO.getEmail());
        user.setPhoneNumber(requestDTO.getPhoneNumber());
        user.setAddress(requestDTO.getAddress());
        user.setGender(requestDTO.getGender() == null ? Gender.OTHER : requestDTO.getGender());
        user.setRole(role);

        userRepository.save(user);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, UserMapper.toDTO(user));
    }

    @Override
    public CommonResponseDTO delete(String id) {
        User user = getEntity(id);

        if (!user.getOrders().isEmpty()) {
            throw new BadRequestException(ErrorMessage.User.ERR_DELETE_HAS_ORDERS);
        }

        if (!user.getReviews().isEmpty()) {
            throw new BadRequestException(ErrorMessage.User.ERR_DELETE_HAS_REVIEWS);
        }

        if (RoleConstant.ROLE_ADMIN.equals(user.getRole().getCode())) {
            throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN_UPDATE_DELETE);
        }

        userRepository.delete(user);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDTO(message);
    }

    @Override
    public PaginationResponseDTO<UserResponseDTO> findAll(PaginationFullRequestDTO requestDTO) {
        Pageable pageable = PaginationUtil.buildPageable(requestDTO, SortByDataConstant.USER);

        Specification<User> spec = UserSpecification.filterByField(requestDTO.getSearchBy(), requestDTO.getKeyword());

        Page<User> page = userRepository.findAll(spec, pageable);

        List<UserResponseDTO> items = page.getContent().stream()
                .map(UserMapper::toDTO)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDTO, SortByDataConstant.USER, page);

        PaginationResponseDTO<UserResponseDTO> responseDTO = new PaginationResponseDTO<>();
        responseDTO.setItems(items);
        responseDTO.setMeta(pagingMeta);

        return responseDTO;
    }

    @Override
    public UserResponseDTO findById(String id) {
        User user = getEntity(id);

        return UserMapper.toDTO(user);
    }

    @Override
    public CommonResponseDTO toggleActive(String id) {
        User user = getEntity(id);

        if (user.getActiveFlag() && RoleConstant.ROLE_ADMIN.equals(user.getRole().getCode())) {
            throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN_UPDATE_DELETE);
        }

        user.setActiveFlag(!user.getActiveFlag());

        userRepository.save(user);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDTO(message, user.getActiveFlag());
    }

    @Override
    public CommonResponseDTO updateCurrentUser(UpdateProfileRequestDTO requestDTO, String userId) {
        User user = getEntity(userId);

        // Kiểm tra email trùng lặp
        if (!requestDTO.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ConflictException(ErrorMessage.Auth.ERR_DUPLICATE_EMAIL);
        }

        user.setFullName(requestDTO.getFullName());
        user.setEmail(requestDTO.getEmail());
        user.setPhoneNumber(requestDTO.getPhoneNumber());
        user.setAddress(requestDTO.getAddress());
        user.setGender(requestDTO.getGender() == null ? Gender.OTHER : requestDTO.getGender());

        userRepository.save(user);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);

        CurrentUserLoginResponseDTO responseDTO = CurrentUserLoginResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .address(user.getAddress())
                .gender(user.getGender())
                .roleNames(Set.of(user.getRole().getCode()))
                .build();
        return new CommonResponseDTO(message, responseDTO);
    }
}
