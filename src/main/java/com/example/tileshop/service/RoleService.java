package com.example.tileshop.service;

import com.example.tileshop.entity.Role;

import java.util.List;

public interface RoleService {
    void init();

    List<Role> getRoles();
}