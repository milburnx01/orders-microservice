package com.example.orderservice.service.impl;

import com.example.orderservice.dto.CreateUserRequest;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.entity.User;
import com.example.orderservice.entity.util.Role;
import com.example.orderservice.exception.UserNotFoundException;
import com.example.orderservice.mapper.UserMapper;
import com.example.orderservice.repository.UserRepository;
import com.example.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(CreateUserRequest createUserRequest) {
        log.info("Create user request for username: {}", createUserRequest.username());
        User user = userMapper.toUser(createUserRequest, passwordEncoder);
        user.setRole(Role.USER);
        userRepository.save(user);
        log.info("User successfully created: userId: {}, username: {}", user.getId(), user.getUsername());
    }

    @Override
    public List<UserResponse> findAll() {
        log.info("Find all users request");
        List<UserResponse> response = userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
        log.info("Successfully found all users. Total users: {}", response.size());
        return response;
    }

    @Override
    public UserResponse findById(UUID id) {
        log.info("Find user by id request: userId: {}", id);
        UserResponse response = userRepository.findById(id)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        log.info("Successfully found user: userId={}", id);
        return response;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.info("Find user by username request: username={}", username);
        Optional<User> response = userRepository.findByUsername(username);
        log.info("Find user by username completed: username={}, found={}", username, response.isPresent());
        return response;
    }

    @Override
    public UserResponse getCurrentUser() {
        log.info("Get current user request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("User not found with name: " + authentication.getName()));
        log.info("Successfully found current user: username={}", user.getUsername());
        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Delete user request: userId={}", id);
        userRepository.deleteById(id);
        log.info("User successfully deleted: userId={}", id);
    }
}
