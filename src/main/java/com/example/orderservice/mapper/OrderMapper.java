package com.example.orderservice.mapper;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "username", source = "user.username")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "createdAt", ignore = true)
    Order toOrder(CreateOrderRequest request, User user);
}
