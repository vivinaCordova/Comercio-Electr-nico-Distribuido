package com.ecommerce.ordenes.controller;

import com.ecommerce.ordenes.service.OrdenValidationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(OrdenValidationException.class)
    ResponseEntity<Map<String, String>> handleValidation(OrdenValidationException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", exception.getMessage()));
    }
}
