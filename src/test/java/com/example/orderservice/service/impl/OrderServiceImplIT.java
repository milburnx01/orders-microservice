package com.example.orderservice.service.impl;

import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.User;
import com.example.orderservice.entity.util.Role;
import com.example.orderservice.entity.util.Status;
import com.example.orderservice.mapper.UserMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.UserRepository;
import com.example.orderservice.security.jwt.JwtProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderServiceImplIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Container
    private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);

    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void shouldReturnAllOrders() {
        User user = new User(null, "admin", passwordEncoder.encode("password"), Role.ADMIN, new ArrayList<>());
        userRepository.save(user);
        Order order = new Order(null, user, "description", Status.CREATED, null);
        orderRepository.save(order);

        String token = jwtProvider.generateAccessToken(user);

        mockMvc.perform(
                        get("/api/orders/all")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }
}
