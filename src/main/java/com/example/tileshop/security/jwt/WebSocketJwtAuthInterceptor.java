package com.example.tileshop.security.jwt;

import com.example.tileshop.service.CustomUserDetailsService;
import com.example.tileshop.service.JwtBlacklistService;
import com.example.tileshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketJwtAuthInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider tokenProvider;

    private final JwtBlacklistService tokenService;

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String jwt = null;

        try {
            if (request instanceof ServletServerHttpRequest) {
                HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

                jwt = JwtUtil.extractTokenFromRequest(servletRequest);

                if (jwt == null) {
                    jwt = servletRequest.getParameter("token");
                }
            }

            if (jwt == null || !tokenProvider.validateToken(jwt) || !tokenProvider.isAccessToken(jwt) || tokenService.isTokenBlocked(jwt)) {
                return false;
            }

            String userId = tokenProvider.extractSubjectFromJwt(jwt);
            UserDetails userDetails = customUserDetailsService.loadUserByUserId(userId);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT Token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}
