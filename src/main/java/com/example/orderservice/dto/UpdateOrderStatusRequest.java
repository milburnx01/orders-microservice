package com.example.orderservice.dto;

import com.example.orderservice.entity.util.Status;

public record UpdateOrderStatusRequest(Status status) {
}
