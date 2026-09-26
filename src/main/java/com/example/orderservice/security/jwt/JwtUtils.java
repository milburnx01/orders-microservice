package com.example.orderservice.security.jwt;

import com.example.orderservice.entity.util.Role;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setUsername(claims.getSubject());
        jwtAuthentication.setRoles(getRoles(claims));
        return jwtAuthentication;
    }

    private static Set<Role> getRoles(Claims claims) {
        String role = claims.get("role", String.class);
        return Set.of(Role.valueOf(role));
    }
}
