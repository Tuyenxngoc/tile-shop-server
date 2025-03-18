package com.example.tileshop.service;

public interface JwtBlacklistService {
    void blacklistAccessToken(String accessToken);

    void blacklistRefreshToken(String refreshToken);

    boolean isTokenBlocked(String token);
}
