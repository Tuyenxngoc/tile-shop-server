package com.example.tileshop.service.impl;

import com.example.tileshop.constant.RoleConstant;
import com.example.tileshop.domain.entity.Role;
import com.example.tileshop.repository.RoleRepository;
import com.example.tileshop.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;

    @Override
    public void init() {
        log.info("Starting role initialization");

        if (roleRepository.count() != 0) {
            log.info("Roles already initialized. Skipping initialization.");
            return;
        }

        Role roleAdmin = Role.builder()
                .code(RoleConstant.ROLE_ADMIN)
                .name(RoleConstant.ROLE_ADMIN.getRoleName())
                .build();

        Role roleUser = Role.builder()
                .code(RoleConstant.ROLE_USER)
                .name(RoleConstant.ROLE_USER.getRoleName())
                .build();

        roleRepository.save(roleAdmin);
        roleRepository.save(roleUser);

        log.info("Role initialization completed.");
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

}
