package com.ecommerce.ordenes.controller;

import com.ecommerce.ordenes.dto.CrearOrdenRequest;
import com.ecommerce.ordenes.dto.OrdenResponse;
import com.ecommerce.ordenes.service.OrdenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {
    private final OrdenService service;

    public OrdenController(OrdenService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenResponse crear(@Valid @RequestBody CrearOrdenRequest request) {
        return service.crear(request);
    }

    // Historial del usuario: las más recientes primero
    @GetMapping
    public List<OrdenResponse> listar(@RequestParam("userId") Long userId) {
        return service.listarPorUsuario(userId);
    }
}
