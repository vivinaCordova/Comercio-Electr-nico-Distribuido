package com.ecommerce.catalogo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        Integer pesoGramos,
        String especificaciones,
        BigDecimal precio,
        Integer stock,
        String sku,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        CategoriaResponse categoria
) {
}
