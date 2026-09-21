package com.example.orderservice.service.impl;

import com.example.orderservice.dto.CreateUserRequest;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.entity.User;
import com.example.orderservice.entity.util.Role;
import com.example.orderservice.exception.UserNotFoundException;
import com.example.orderservice.mapper.UserMapper;
import com.example.orderservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createUser_shouldMapAndSaveUser() {
        CreateUserRequest request = new CreateUserRequest("username", "password");
        User user = new User(UUID.randomUUID(), request.username(), request.password(), Role.USER, List.of());
        when(userMapper.toUser(request, passwordEncoder))
                .thenReturn(user);

        userService.createUser(request);

        verify(userMapper).toUser(request, passwordEncoder);
        verify(userRepository).save(user);
    }

    @Test
    void findAll_shouldReturnUsers() {

        User user1 = new User(UUID.randomUUID(), "user1", "password", Role.USER, List.of());
        User user2 = new User(UUID.randomUUID(), "user2", "password", Role.USER, List.of());

        UserResponse response1 = new UserResponse(user1.getId(), user1.getUsername(), user1.getRole());
        UserResponse response2 = new UserResponse(user2.getId(), user2.getUsername(), user2.getRole());

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        when(userMapper.toUserResponse(user1))
                .thenReturn(response1);

        when(userMapper.toUserResponse(user2))
                .thenReturn(response2);


        List<UserResponse> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(List.of(response1, response2), result);
        verify(userRepository).findAll();
        verify(userMapper).toUserResponse(user1);
        verify(userMapper).toUserResponse(user2);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoUsersExist() {
        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserResponse> result = userService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        UUID id = UUID.randomUUID();

        User user = new User(id, "user", "password", Role.USER, List.of());
        UserResponse response = new UserResponse(id, user.getUsername(), user.getRole());

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponse(user))
                .thenReturn(response);

        UserResponse result = userService.findById(id);

        assertNotNull(result);
        assertSame(response, result);

        verify(userRepository).findById(id);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void findById_shouldThrowException_whenUserDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findById(id)
        );

        assertEquals(
                "User not found with id: " + id,
                exception.getMessage()
        );

        verify(userRepository).findById(id);
        verifyNoInteractions(userMapper);
    }

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        String username = "user";

        User user = new User(UUID.randomUUID(), "user", "password", Role.USER, List.of());

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertSame(user, result.get());

        verify(userRepository).findByUsername(username);
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUserDoesNotExist() {
        String username = "user";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());


        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isEmpty());

        verify(userRepository).findByUsername(username);
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUser() {
        User user = new User(UUID.randomUUID(), "user", "password", Role.USER, List.of());
        UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getRole());

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponse(user))
                .thenReturn(response);

        UserResponse result = userService.getCurrentUser();

        assertSame(response, result);

        verify(userRepository).findByUsername("user");
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void getCurrentUser_shouldThrowException_whenUserDoesNotExist() {
        String username = "user";
        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn(username);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getCurrentUser()
        );

        assertEquals(
                "User not found with name: " + username,
                exception.getMessage()
        );

        verify(userRepository).findByUsername(username);
        verifyNoInteractions(userMapper);
    }


    @Test
    void deleteById_shouldDeleteUser() {
        UUID id = UUID.randomUUID();

        userService.deleteById(id);

        verify(userRepository).deleteById(id);
    }
}
