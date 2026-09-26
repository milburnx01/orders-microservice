package com.example.orderservice.controller;

import com.example.orderservice.dto.AccessTokenRequest;
import com.example.orderservice.dto.CreateUserRequest;
import com.example.orderservice.dto.JwtRequest;
import com.example.orderservice.dto.JwtResponse;
import com.example.orderservice.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Авторизация", description = "Регистрация, аутентификация и авторизация пользователя в системе")
public interface AuthApi {

    @Operation(summary = "Регистрация", description = "Регистрация пользователя в системе")
    ResponseEntity<String> registerUser(@RequestBody @Valid CreateUserRequest createUserRequest);

    @Operation(summary = "Вход в систему", description = "Вход пользователя в систему")
    ResponseEntity<JwtResponse> login(@RequestBody @Valid JwtRequest jwtRequest);

    @Operation(summary = "Текущий пользователь", description = "Получение информации о текущем пользователе")
    @SecurityRequirement(name = "JWT")
    ResponseEntity<UserResponse> getCurrentUser();

    @Operation(summary = "Получение токена доступа", description = "Получение токена доступа по refresh токену")
    ResponseEntity<JwtResponse> getToken(@RequestBody AccessTokenRequest accessTokenRequest);
}
