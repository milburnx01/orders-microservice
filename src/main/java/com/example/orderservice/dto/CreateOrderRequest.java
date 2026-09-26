package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(@NotBlank(message = "description must not be blank")
                                 String description) {
}
