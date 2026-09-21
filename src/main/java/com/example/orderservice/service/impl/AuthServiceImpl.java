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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtResponse login(JwtRequest jwtRequest) {
        User user = userService.findByUsername(jwtRequest.username()).orElseThrow(() -> new AuthException("Invalid username"));
        if (passwordEncoder.matches(jwtRequest.password(), user.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(user);
            String refreshToken = jwtProvider.generateRefreshToken(user);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Invalid password");
        }
    }

    @Override
    public JwtResponse getAccessToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String username = claims.getSubject();
            User user = userService.findByUsername(username).orElseThrow(() -> new AuthException("Invalid username"));
            String accessToken = jwtProvider.generateAccessToken(user);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Invalid refresh token");
        }
    }

}
