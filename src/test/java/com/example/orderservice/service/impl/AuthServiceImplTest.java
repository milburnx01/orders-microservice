package com.example.orderservice.service.impl;

import com.example.orderservice.dto.JwtRequest;
import com.example.orderservice.dto.JwtResponse;
import com.example.orderservice.entity.User;
import com.example.orderservice.entity.util.Role;
import com.example.orderservice.exception.AuthException;
import com.example.orderservice.security.jwt.JwtProvider;
import com.example.orderservice.service.UserService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Claims claims;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "user", "password", Role.USER, List.of());
    }

    @Test
    void login_shouldReturnJwtResponse_whenCredentialsAreValid() {
        // Arrange
        JwtRequest request = new JwtRequest("user", "password");

        when(userService.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.password(), user.getPassword()))
                .thenReturn(true);

        when(jwtProvider.generateAccessToken(user))
                .thenReturn("access-token");

        when(jwtProvider.generateRefreshToken(user))
                .thenReturn("refresh-token");

        JwtResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());

        verify(userService).findByUsername("user");
        verify(passwordEncoder).matches(request.password(), user.getPassword());
        verify(jwtProvider).generateAccessToken(user);
        verify(jwtProvider).generateRefreshToken(user);
    }

    @Test
    void login_shouldThrowAuthException_whenUserNotFound() {
        // Arrange
        JwtRequest request = new JwtRequest("unknownUser", "password");

        when(userService.findByUsername("unknownUser"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasMessage("Invalid username");

        verify(userService).findByUsername("unknownUser");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void login_shouldThrowAuthException_whenPasswordIsInvalid() {

        JwtRequest request = new JwtRequest("user", "wrongPassword");

        when(userService.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.password(), user.getPassword()))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(AuthException.class)
                .hasMessage("Invalid password");

        verify(userService).findByUsername("user");
        verify(passwordEncoder).matches(request.password(), user.getPassword());
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void getAccessToken_shouldReturnNewAccessToken_whenRefreshTokenIsValid() {
        // Arrange
        String refreshToken = "valid-refresh-token";

        when(jwtProvider.validateRefreshToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.getRefreshClaims(refreshToken))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn("user");

        when(userService.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(jwtProvider.generateAccessToken(user))
                .thenReturn("new-access-token");

        JwtResponse response = authService.getAccessToken(refreshToken);

        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
        assertEquals(refreshToken, response.refreshToken());

        verify(jwtProvider).validateRefreshToken(refreshToken);
        verify(jwtProvider).getRefreshClaims(refreshToken);
        verify(claims).getSubject();
        verify(userService).findByUsername("user");
        verify(jwtProvider).generateAccessToken(user);
    }

    @Test
    void getAccessToken_shouldThrowAuthException_whenRefreshTokenIsInvalid() {
        // Arrange
        String refreshToken = "invalid-refresh-token";

        when(jwtProvider.validateRefreshToken(refreshToken))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.getAccessToken(refreshToken))
        .isInstanceOf(AuthException.class)
                .hasMessage("Invalid refresh token");

        verify(jwtProvider).validateRefreshToken(refreshToken);
        verify(jwtProvider, never()).getRefreshClaims(anyString());
        verifyNoInteractions(userService);
    }

    @Test
    void getAccessToken_shouldThrowAuthException_whenUserFromRefreshTokenNotFound() {
        String refreshToken = "valid-refresh-token";

        when(jwtProvider.validateRefreshToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.getRefreshClaims(refreshToken))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn("unknownUser");

        when(userService.findByUsername("unknownUser"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getAccessToken(refreshToken))
        .isInstanceOf(AuthException.class)
                .hasMessage("Invalid username");


        verify(jwtProvider).validateRefreshToken(refreshToken);
        verify(jwtProvider).getRefreshClaims(refreshToken);
        verify(claims).getSubject();
        verify(userService).findByUsername("unknownUser");
        verify(jwtProvider, never()).generateAccessToken(any(User.class));
    }
}