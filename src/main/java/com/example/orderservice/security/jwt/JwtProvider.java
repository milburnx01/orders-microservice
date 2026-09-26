package com.example.orderservice.security.jwt;

import com.example.orderservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(@Value("${jwt.secret.access}") String jwtAccessSecret, @Value("${jwt.secret.refresh}") String jwtRefreshSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(User user) {
        Instant accessExpirationInstant = LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant();
        Date acessExpirationDate = Date.from(accessExpirationInstant);
        return Jwts.builder().subject(user.getUsername())
                .expiration(acessExpirationDate)
                .signWith(jwtAccessSecret)
                .claim("role", user.getRole())
                .claim("username", user.getUsername())
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant refreshExpirationInstant = LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        Date refreshExpirationDate = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .subject(user.getUsername())
                .expiration(refreshExpirationDate)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(String token, SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getAccessClaims(String accessToken) {
        return getClaims(accessToken, jwtAccessSecret);
    }

    public Claims getRefreshClaims(String refreshToken) {
        return getClaims(refreshToken, jwtRefreshSecret);
    }

    private Claims getClaims(String token, SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
