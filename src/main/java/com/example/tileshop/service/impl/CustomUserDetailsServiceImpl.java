package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.entity.User;
import com.example.tileshop.exception.UnauthorizedException;
import com.example.tileshop.repository.UserRepository;
import com.example.tileshop.security.CustomUserDetails;
import com.example.tileshop.service.CustomUserDetailsService;
import com.example.tileshop.util.MessageUtil;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    UserRepository userRepository;

    MessageUtil messageUtil;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(messageUtil.getMessage(ErrorMessage.User.ERR_NOT_FOUND_USERNAME, username)));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getCode().name()));

        return CustomUserDetails.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .enabled(user.getActiveFlag())
                .authorities(authorities)
                .build();
    }

    @Override
    @Transactional
    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, UnauthorizedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(messageUtil.getMessage(ErrorMessage.User.ERR_NOT_FOUND_ID, userId)));

        if (!user.getActiveFlag()) {
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_ACCOUNT_DISABLED);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getCode().name()));

        return CustomUserDetails.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .enabled(user.getActiveFlag())
                .authorities(authorities)
                .build();
    }

}