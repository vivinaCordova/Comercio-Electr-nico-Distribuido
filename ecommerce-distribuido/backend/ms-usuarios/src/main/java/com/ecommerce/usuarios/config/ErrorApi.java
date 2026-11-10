package com.ecommerce.usuarios.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class ErrorApi extends RuntimeException {
    private final HttpStatus estado;

    public ErrorApi() { this(HttpStatus.INTERNAL_SERVER_ERROR, ""); }
    public ErrorApi(HttpStatus estado, String mensaje) { super(mensaje); this.estado = estado; }

    @ExceptionHandler(ErrorApi.class)
    public ResponseEntity<Map<String, String>> negocio(ErrorApi e) {
        return ResponseEntity.status(e.estado).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream().findFirst()
            .map(f -> f.getDefaultMessage()).orElse("Datos inválidos.");
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }
}
