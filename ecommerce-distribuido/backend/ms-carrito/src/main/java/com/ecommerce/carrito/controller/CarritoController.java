package com.ecommerce.carrito.controller;

import com.ecommerce.carrito.dto.CarritoDto;
import com.ecommerce.carrito.dto.ItemRequest;
import com.ecommerce.carrito.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {
    private final CarritoService service;

    public CarritoController(CarritoService service) { this.service = service; }

    @GetMapping("/{usuarioId}")
    public CarritoDto obtener(@PathVariable Long usuarioId) { return service.obtener(usuarioId); }

    @PostMapping("/{usuarioId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CarritoDto agregar(@PathVariable Long usuarioId, @Valid @RequestBody ItemRequest req) {
        return service.agregar(usuarioId, req.productoId(), req.cantidad());
    }

    @PutMapping("/{usuarioId}/items/{productoId}")
    public CarritoDto cambiarCantidad(@PathVariable Long usuarioId, @PathVariable Long productoId,
                                      @Valid @RequestBody ItemRequest req) {
        return service.cambiarCantidad(usuarioId, productoId, req.cantidad());
    }

    @DeleteMapping("/{usuarioId}/items/{productoId}")
    public CarritoDto quitar(@PathVariable Long usuarioId, @PathVariable Long productoId) {
        return service.quitar(usuarioId, productoId);
    }

    @DeleteMapping("/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void vaciar(@PathVariable Long usuarioId) { service.vaciar(usuarioId); }
}
