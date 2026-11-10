package com.ecommerce.usuarios.controller;

import com.ecommerce.usuarios.dto.Dtos.*;
import com.ecommerce.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) { this.servicio = servicio; }

    @PostMapping("/registro") @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto registro(@Valid @RequestBody RegistroRequest r) { return servicio.registrar(r); }

    @PostMapping("/login")
    public UsuarioDto login(@Valid @RequestBody LoginRequest r) { return servicio.login(r); }
}
