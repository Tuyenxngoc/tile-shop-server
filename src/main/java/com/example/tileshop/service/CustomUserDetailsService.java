package com.example.tileshop.service;

import com.example.tileshop.exception.UnauthorizedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomUserDetailsService extends UserDetailsService {
    UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, UnauthorizedException;
}
