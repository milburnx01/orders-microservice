package com.example.orderservice.exception.handler;

import com.example.orderservice.dto.ErrorResponse;
import com.example.orderservice.dto.ValidationErrorResponse;
import com.example.orderservice.dto.Violation;
import com.example.orderservice.exception.AuthException;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .toList();
        log.error("Validation errors: {}", violations);
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(violations));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException e) {
        log.error("Authentication error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({OrderNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e) {
        log.error("Resource not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

}
