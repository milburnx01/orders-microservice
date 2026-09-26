package com.example.orderservice.mapper;

import com.example.orderservice.dto.CreateUserRequest;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.entity.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(createUserRequest.password()))")
    User toUser(CreateUserRequest createUserRequest, @Context PasswordEncoder passwordEncoder);

}
