package com.example.orderservice.service;

import com.example.orderservice.dto.JwtResponse;
import com.example.orderservice.dto.JwtRequest;

public interface AuthService {

    JwtResponse login(JwtRequest jwtRequest);

    JwtResponse getAccessToken(String refreshToken);

}
