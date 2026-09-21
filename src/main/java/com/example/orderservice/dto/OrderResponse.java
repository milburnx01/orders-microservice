package com.example.orderservice.dto;

import com.example.orderservice.entity.util.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(UUID id, String username, String description, Status status, LocalDateTime createdAt) {
}
