
package com.ecommerce.catalogo.dto;

public record ProductoImagenResponse(
    Long id,
    Long productoId,
    String url,
    Boolean esPrincipal,
    Integer orden
) {
}
