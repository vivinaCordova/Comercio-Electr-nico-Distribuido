package com.ecommerce.catalogo.dto;

import com.ecommerce.catalogo.entity.Categoria;

// cantidadProductos solo se llena al listar categorías (productos activos); dentro de un producto va null
public record CategoriaResponse(
        Long id,
        String nombre,
        String slug,
        String icono,
        String descripcion,
        Long cantidadProductos
) {

    public static CategoriaResponse de(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.getSlug(),
                categoria.getIcono(), categoria.getDescripcion(), null);
    }
}
