package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JwtRequest(
        @NotBlank(message = "Username must not be empty.")
        String username,
        @NotNull(message = "Password must not be null")
        String password) {
}
