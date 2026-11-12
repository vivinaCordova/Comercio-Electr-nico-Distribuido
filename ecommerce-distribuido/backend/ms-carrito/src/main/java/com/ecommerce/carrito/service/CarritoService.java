package com.ecommerce.carrito.service;

import com.ecommerce.carrito.dto.CarritoDto;
import com.ecommerce.carrito.entity.Carrito;
import com.ecommerce.carrito.repository.CarritoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CarritoService {
    private final CarritoRepository repo;

    public CarritoService(CarritoRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public CarritoDto obtener(Long usuarioId) {
        return CarritoDto.de(repo.findByUsuarioId(usuarioId).orElseGet(() -> Carrito.paraUsuario(usuarioId)));
    }

    public CarritoDto agregar(Long usuarioId, Long productoId, int cantidad) {
        Carrito c = repo.findByUsuarioId(usuarioId).orElseGet(() -> Carrito.paraUsuario(usuarioId));
        c.agregar(productoId, cantidad);
        return CarritoDto.de(repo.save(c));
    }

    public CarritoDto cambiarCantidad(Long usuarioId, Long productoId, int cantidad) {
        Carrito c = existente(usuarioId);
        if (!c.cambiarCantidad(productoId, cantidad)) throw noEncontrado("Producto no está en el carrito");
        return CarritoDto.de(c);
    }

    public CarritoDto quitar(Long usuarioId, Long productoId) {
        Carrito c = existente(usuarioId);
        if (!c.quitar(productoId)) throw noEncontrado("Producto no está en el carrito");
        return CarritoDto.de(c);
    }

    public void vaciar(Long usuarioId) {
        repo.findByUsuarioId(usuarioId).ifPresent(Carrito::vaciar);
    }

    private Carrito existente(Long usuarioId) {
        return repo.findByUsuarioId(usuarioId).orElseThrow(() -> noEncontrado("Carrito no existe"));
    }

    private ResponseStatusException noEncontrado(String msg) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
    }
}
