package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UpdateOrderStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    Page<OrderResponse> findAllByCurrentUser(Pageable pageable);

    void createOrder(CreateOrderRequest createOrderRequest);

    Page<OrderResponse> findAll(Pageable pageable);

    void updateOrderStatus(UUID id, UpdateOrderStatusRequest updateOrderStatusRequest);

    void deleteOrder(UUID id);
}
