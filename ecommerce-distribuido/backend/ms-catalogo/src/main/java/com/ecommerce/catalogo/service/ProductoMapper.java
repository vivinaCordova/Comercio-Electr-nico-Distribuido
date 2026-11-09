package com.ecommerce.catalogo.service;

import com.ecommerce.catalogo.dto.ProductoCreateRequest;
import com.ecommerce.catalogo.dto.ProductoResponse;
import com.ecommerce.catalogo.dto.ProductoUpdateRequest;
import com.ecommerce.catalogo.entity.Producto;

final class ProductoMapper {

    private ProductoMapper() {
    }

    static Producto toEntity(ProductoCreateRequest request, String skuNormalizado) {
        Producto producto = new Producto();
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setSku(skuNormalizado);
        producto.setActivo(request.activo() != null ? request.activo() : Boolean.TRUE);
        return producto;
    }

    static void aplicarActualizacion(Producto producto, ProductoUpdateRequest request, String skuNormalizado) {
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setSku(skuNormalizado);
        if (request.activo() != null) {
            producto.setActivo(request.activo());
        }
    }

    static ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getSku(),
                producto.getActivo(),
                producto.getFechaCreacion(),
                producto.getFechaActualizacion());
    }
}
