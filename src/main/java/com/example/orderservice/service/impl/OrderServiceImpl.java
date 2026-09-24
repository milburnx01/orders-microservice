package com.example.orderservice.service.impl;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UpdateOrderStatusRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.User;
import com.example.orderservice.entity.util.Status;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public Page<OrderResponse> findAllByCurrentUser(Pageable pageable) {
        log.info("Find all orders by current user. Pageable: {}", pageable);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName()).get();

        Page<OrderResponse> response = orderRepository.findAllByUser(user, pageable).map(orderMapper::toOrderResponse);
        log.info("Successfully found orders for current user. Total elements: {}", response.getTotalElements());
        return response;
    }

    @Override
    public void createOrder(CreateOrderRequest createOrderRequest) {
        log.info("Create order request: {}", createOrderRequest);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).get();
        Order order = orderMapper.toOrder(createOrderRequest, user);
        order.setStatus(Status.CREATED);
        orderRepository.save(order);
        log.info("Order successfully created. Order id: {}", order.getId());
    }

    @Override
    public Page<OrderResponse> findAll(Pageable pageable) {
        log.info("Find all orders request. Pageable: {}", pageable);
        Page<OrderResponse> response = orderRepository.findAll(pageable).map(orderMapper::toOrderResponse);
        log.info("Successfully found all orders. Total elements: {}",
                response.getTotalElements());
        return response;
    }

    @Override
    public void updateOrderStatus(UUID id, UpdateOrderStatusRequest updateOrderStatusRequest) {
        log.info("Update order status request. Order id: {}, new status: {}",
                id, updateOrderStatusRequest.status());
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setStatus(updateOrderStatusRequest.status());
        orderRepository.save(order);
        log.info("Order status successfully updated. Order id: {}, status: {}", id, updateOrderStatusRequest.status());
    }

    @Override
    public void deleteOrder(UUID id) {
        log.info("Delete order request. Order id: {}", id);
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
        boolean isOwner = order.getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!isAdmin && !isOwner) {
            throw new OrderNotFoundException("No access to the order");
        }
        orderRepository.deleteById(id);
        log.info("Order successfully deleted. Order id: {}", id);
    }
}
