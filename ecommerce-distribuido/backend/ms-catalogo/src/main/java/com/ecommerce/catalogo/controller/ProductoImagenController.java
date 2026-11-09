
package com.ecommerce.catalogo.controller;

import com.ecommerce.catalogo.dto.ProductoImagenResponse;
import com.ecommerce.catalogo.service.ProductoImagenService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/catalogo/productos/{productoId}/imagenes")
public class ProductoImagenController {

    private final ProductoImagenService imagenService;

    public ProductoImagenController(ProductoImagenService imagenService) {
        this.imagenService = imagenService;
    }

    // Listar las imagenes de un producto
    @GetMapping
    public ResponseEntity<List<ProductoImagenResponse>> listar(
            @PathVariable Long productoId) {

        return ResponseEntity.ok(imagenService.listar(productoId));
    }

    // Subir una fotografia
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoImagenResponse> subir(
            @PathVariable Long productoId,
            @RequestParam("archivo") MultipartFile archivo)
            throws IOException {

        ProductoImagenResponse respuesta =
                imagenService.subir(productoId, archivo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    // Visualizar una fotografia
    @GetMapping("/{imagenId}/archivo")
    public ResponseEntity<Resource> visualizar(
            @PathVariable Long productoId,
            @PathVariable Long imagenId) throws IOException {

        Resource archivo = imagenService.obtenerArchivo(
                productoId, imagenId
        );

        String nombre = archivo.getFilename();

        MediaType tipo = MediaType.APPLICATION_OCTET_STREAM;

        if (nombre != null) {
            String nombreMinuscula = nombre.toLowerCase();

            if (nombreMinuscula.endsWith(".jpg")
                    || nombreMinuscula.endsWith(".jpeg")) {
                tipo = MediaType.IMAGE_JPEG;
            } else if (nombreMinuscula.endsWith(".png")) {
                tipo = MediaType.IMAGE_PNG;
            } else if (nombreMinuscula.endsWith(".webp")) {
                tipo = MediaType.parseMediaType("image/webp");
            }
        }

        return ResponseEntity.ok()
                .contentType(tipo)
                .header("X-Content-Type-Options", "nosniff")
                .body(archivo);
    }

    // Eliminar una fotografia
    @DeleteMapping("/{imagenId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long productoId,
            @PathVariable Long imagenId) throws IOException {

        imagenService.eliminar(productoId, imagenId);

        return ResponseEntity.noContent().build();
    }

    // Establecer la imagen principal de un producto
    @PatchMapping("/{imagenId}/principal")
    public ResponseEntity<ProductoImagenResponse> establecerPrincipal(
            @PathVariable Long productoId,
            @PathVariable Long imagenId) {

        ProductoImagenResponse respuesta =
                imagenService.establecerPrincipal(productoId, imagenId);

        return ResponseEntity.ok(respuesta);
    }

}
