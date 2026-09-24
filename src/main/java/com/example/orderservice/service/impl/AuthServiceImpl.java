package com.example.orderservice.service.impl;

import com.example.orderservice.dto.JwtRequest;
import com.example.orderservice.dto.JwtResponse;
import com.example.orderservice.entity.User;
import com.example.orderservice.exception.AuthException;
import com.example.orderservice.security.jwt.JwtProvider;
import com.example.orderservice.service.AuthService;
import com.example.orderservice.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtResponse login(JwtRequest jwtRequest) {
        log.info("Login request for username: {}", jwtRequest.username());
        User user = userService.findByUsername(jwtRequest.username()).orElseThrow(() -> new AuthException("Invalid username"));
        if (passwordEncoder.matches(jwtRequest.password(), user.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(user);
            String refreshToken = jwtProvider.generateRefreshToken(user);
            log.info("User successfully authenticated: {}", jwtRequest.username());
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Invalid password");
        }
    }

    @Override
    public JwtResponse getAccessToken(String refreshToken) {
        log.info("Get access token request");
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String username = claims.getSubject();
            User user = userService.findByUsername(username).orElseThrow(() -> new AuthException("Invalid username"));
            String accessToken = jwtProvider.generateAccessToken(user);
            log.info("Access token successfully generated for username: {}", username);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Invalid refresh token");
        }
    }

}
