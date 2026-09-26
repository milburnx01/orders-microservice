package com.example.orderservice.service;

import com.example.orderservice.dto.CreateUserRequest;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    void createUser(CreateUserRequest createUserRequest);

    List<UserResponse> findAll();

    UserResponse findById(UUID id);

    Optional<User> findByUsername(String username);

    UserResponse getCurrentUser();

    void deleteById(UUID id);
}
