package com.ecommerce.catalogo.controller;

import com.ecommerce.catalogo.dto.PageResponse;
import com.ecommerce.catalogo.dto.ProductoCreateRequest;
import com.ecommerce.catalogo.dto.ProductoResponse;
import com.ecommerce.catalogo.dto.ProductoUpdateRequest;
import com.ecommerce.catalogo.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/catalogo/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoCreateRequest request) {
        ProductoResponse creado = productoService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @GetMapping
    public PageResponse<ProductoResponse> listar(
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanio", defaultValue = "20") int tamanio,
            @RequestParam(name = "ordenarPor", defaultValue = "id") String ordenarPor,
            @RequestParam(name = "direccion", defaultValue = "asc") String direccion) {
        return productoService.listar(pagina, tamanio, ordenarPor, direccion);
    }

    @GetMapping("/activos")
    public PageResponse<ProductoResponse> listarActivos(
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanio", defaultValue = "20") int tamanio,
            @RequestParam(name = "ordenarPor", defaultValue = "id") String ordenarPor,
            @RequestParam(name = "direccion", defaultValue = "asc") String direccion) {
        return productoService.listarActivos(pagina, tamanio, ordenarPor, direccion);
    }

    @GetMapping("/buscar")
    public PageResponse<ProductoResponse> buscar(
            @RequestParam(name = "nombre") String nombre,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanio", defaultValue = "20") int tamanio,
            @RequestParam(name = "ordenarPor", defaultValue = "id") String ordenarPor,
            @RequestParam(name = "direccion", defaultValue = "asc") String direccion) {
        return productoService.buscarPorNombre(nombre, pagina, tamanio, ordenarPor, direccion);
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable("id") Long id) {
        return productoService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable("id") Long id,
                                       @Valid @RequestBody ProductoUpdateRequest request) {
        return productoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable("id") Long id) {
        productoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
