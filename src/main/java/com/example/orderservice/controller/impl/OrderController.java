package com.example.orderservice.controller.impl;

import com.example.orderservice.controller.OrderApi;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UpdateOrderStatusRequest;
import com.example.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @GetMapping
    @Override
    public ResponseEntity<Page<OrderResponse>> findAllOrdersByCurrentUser(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                                          @RequestParam(required = false, defaultValue = "20") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.findAllByCurrentUser(pageable));
    }

    @PostMapping
    @Override
    public ResponseEntity<Void> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        orderService.createOrder(createOrderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/all")
    @Override
    public ResponseEntity<Page<OrderResponse>> findAllOrders(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                             @RequestParam(required = false, defaultValue = "20") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<Void> updateOrderStatus(@PathVariable UUID id, @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        orderService.updateOrderStatus(id, updateOrderStatusRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
