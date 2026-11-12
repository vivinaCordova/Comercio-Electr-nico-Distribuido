package com.ecommerce.catalogo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductoDetalleResponse(
        Long id, String nombre, String descripcion,         Integer pesoGramos, String especificaciones,
        BigDecimal precio, Integer stock, String sku, Boolean activo,
        LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
        CategoriaResponse categoria, List<ProductoImagenResponse> imagenes
) {
}
