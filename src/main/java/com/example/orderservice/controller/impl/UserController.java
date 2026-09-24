package com.example.orderservice.controller.impl;

import com.example.orderservice.controller.UserApi;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

    private final UserService userService;

    @GetMapping
    @Override
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
