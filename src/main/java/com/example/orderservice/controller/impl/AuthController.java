package com.example.orderservice.controller.impl;

import com.example.orderservice.controller.AuthApi;
import com.example.orderservice.dto.AccessTokenRequest;
import com.example.orderservice.dto.CreateUserRequest;
import com.example.orderservice.dto.JwtRequest;
import com.example.orderservice.dto.JwtResponse;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.service.AuthService;
import com.example.orderservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    @Override
    public ResponseEntity<String> registerUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid JwtRequest jwtRequest) {
        JwtResponse jwtResponse = authService.login(jwtRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse userResponse = userService.getCurrentUser();
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/token")
    @Override
    public ResponseEntity<JwtResponse> getToken(@RequestBody AccessTokenRequest accessTokenRequest) {
        JwtResponse jwtResponse = authService.getAccessToken(accessTokenRequest.refreshToken());
        return ResponseEntity.ok(jwtResponse);
    }
}
