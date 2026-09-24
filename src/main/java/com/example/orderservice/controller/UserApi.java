package com.example.orderservice.controller;

import com.example.orderservice.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Tag(name = "Пользователи", description = "Управление пользователями")
@SecurityRequirement(name = "JWT")
public interface UserApi {

    @Operation(summary = "Общий список пользователей")
    List<UserResponse> findAll();

    @Operation(summary = "Удаление пользователя")
    ResponseEntity<Void> deleteById(@PathVariable UUID id);
}
