package com.example.orderservice.controller;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UpdateOrderStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Tag(name = "Заказы", description = "Управление заказами")
@SecurityRequirement(name = "JWT")
public interface OrderApi {

    @Operation(summary = "Все заказы пользователя", description = "Получение списка заказов текущего пользователя")
    ResponseEntity<Page<OrderResponse>> findAllOrdersByCurrentUser(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                                   @RequestParam(required = false, defaultValue = "20") Integer size);
    @Operation(summary = "Создание заказа")
    ResponseEntity<Void> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest);

    @Operation(summary = "Все заказы", description = "Получения списка заказов всех пользователей")
    ResponseEntity<Page<OrderResponse>> findAllOrders(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                      @RequestParam(required = false, defaultValue = "20") Integer size);

    @Operation(summary = "Обновление заказа")
    ResponseEntity<Void> updateOrderStatus(@PathVariable UUID id, @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest);

    @Operation(summary = "Удаление заказа")
    ResponseEntity<Void> deleteOrder(@PathVariable UUID id);
}
