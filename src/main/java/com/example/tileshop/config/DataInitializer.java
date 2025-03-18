package com.example.tileshop.config;

import com.example.tileshop.service.RoleService;
import com.example.tileshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleService roleService;

    private final UserService userService;

    @Override
    public void run(String... args) {
        roleService.init();
        userService.init();
    }

}
