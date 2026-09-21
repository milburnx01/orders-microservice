package com.example.orderservice.service.impl;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UpdateOrderStatusRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.User;
import com.example.orderservice.entity.util.Role;
import com.example.orderservice.entity.util.Status;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Order order;
    private OrderResponse orderResponse;
    private UUID orderId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);

        orderId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);

        user = new User(UUID.randomUUID(), "user", "password", Role.USER, List.of());

        order = new Order(orderId, user, "some description", Status.CREATED, LocalDateTime.now());

        orderResponse = new OrderResponse(orderId, user.getUsername(), order.getDescription(), order.getStatus(), order.getCreatedAt());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAllByCurrentUser_shouldReturnPageOfOrderResponses() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(Optional.of(user));

        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findAllByUser(user, pageable)).thenReturn(orderPage);
        when(orderMapper.toOrderResponse(order)).thenReturn(orderResponse);

        Page<OrderResponse> result = orderService.findAllByCurrentUser(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(orderResponse);

        verify(userService).findByUsername("user");
        verify(orderRepository).findAllByUser(user, pageable);
        verify(orderMapper).toOrderResponse(order);
    }

    @Test
    void findAllByCurrentUser_shouldReturnEmptyPage_whenNoOrders() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(Optional.of(user));

        Page<Order> emptyPage = new PageImpl<>(Collections.emptyList());
        when(orderRepository.findAllByUser(user, pageable)).thenReturn(emptyPage);

        Page<OrderResponse> result = orderService.findAllByCurrentUser(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(orderRepository).findAllByUser(user, pageable);
        verifyNoInteractions(orderMapper);
    }


    @Test
    void createOrder_shouldSaveOrderWithCurrentUser() {
        CreateOrderRequest request = new CreateOrderRequest("order description");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(orderMapper.toOrder(request, user)).thenReturn(order);

        orderService.createOrder(request);

        verify(userService).findByUsername("user");
        verify(orderMapper).toOrder(request, user);
        verify(orderRepository).save(order);
    }


    @Test
    void findAll_shouldReturnPageOfOrderResponses() {
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        when(orderMapper.toOrderResponse(order)).thenReturn(orderResponse);

        Page<OrderResponse> result = orderService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(orderResponse);

        verify(orderRepository).findAll(pageable);
        verify(orderMapper).toOrderResponse(order);
    }

    @Test
    void findAll_shouldReturnEmptyPage_whenNoOrders() {
        Page<Order> emptyPage = new PageImpl<>(Collections.emptyList());
        when(orderRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<OrderResponse> result = orderService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(orderRepository).findAll(pageable);
        verifyNoInteractions(orderMapper);
    }


    @Test
    void updateOrderStatus_shouldUpdateAndSaveOrder() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(Status.IN_PROGRESS);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.updateOrderStatus(orderId, request);

        assertThat(order.getStatus()).isEqualTo(Status.IN_PROGRESS);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_shouldThrowOrderNotFoundException_whenOrderNotFound() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(Status.IN_PROGRESS);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found");

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }


    @Test
    void deleteOrder_shouldDeleteOrder_whenUserIsAdmin() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        doReturn(List.of(new SimpleGrantedAuthority("ADMIN")))
                .when(authentication).getAuthorities();

        orderService.deleteOrder(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void deleteOrder_shouldDeleteOrder_whenUserIsOwner() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        doReturn(List.of(new SimpleGrantedAuthority("USER"))).when(authentication).getAuthorities();

        orderService.deleteOrder(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void deleteOrder_shouldThrowOrderNotFoundException_whenOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found");

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void deleteOrder_shouldThrowOrderNotFoundException_whenUserIsNotOwnerAndNotAdmin() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("anotheruser");
        doReturn(List.of(new SimpleGrantedAuthority("USER"))).when(authentication).getAuthorities();

        assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("No access to the order");

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).deleteById(any());
    }
}