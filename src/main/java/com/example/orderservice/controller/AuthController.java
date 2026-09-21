package com.example.orderservice.controller;

import com.example.orderservice.dto.AccessTokenRequest;
import com.example.orderservice.dto.CreateUserRequest;
import com.example.orderservice.dto.JwtRequest;
import com.example.orderservice.dto.JwtResponse;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.service.AuthService;
import com.example.orderservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Авторизация", description = "Регистрация, аутентификация и авторизация пользователя в системе")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Регистрация", description = "Регистрация пользователя в системе")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @Operation(summary = "Вход в систему", description = "Вход пользователя в систему")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid JwtRequest jwtRequest) {
        JwtResponse jwtResponse = authService.login(jwtRequest);
        return ResponseEntity.ok(jwtResponse);
    }
    @Operation(summary = "Текущий пользователь", description = "Получение информации о текущем пользователе")
    @GetMapping("/me")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse userResponse = userService.getCurrentUser();
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Получение токена доступа", description = "Получение токена доступа по refresh токену")
    @GetMapping("/token")
    public ResponseEntity<JwtResponse> getToken(@RequestBody AccessTokenRequest accessTokenRequest) {
        JwtResponse jwtResponse = authService.getAccessToken(accessTokenRequest.refreshToken());
        return ResponseEntity.ok(jwtResponse);
    }
}
