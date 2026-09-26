package com.example.orderservice.dto;

import com.example.orderservice.entity.util.Role;

import java.util.UUID;

public record UserResponse(UUID id, String username, Role role) {
}
