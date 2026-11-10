package com.ecommerce.carrito.dto;

import com.ecommerce.carrito.entity.Carrito;
import java.util.List;

public record CarritoDto(Long usuarioId, List<ItemDto> items) {
    public record ItemDto(Long productoId, Integer cantidad) {}

    public static CarritoDto de(Carrito c) {
        return new CarritoDto(c.getUsuarioId(),
            c.getItems().stream().map(i -> new ItemDto(i.getProductoId(), i.getCantidad())).toList());
    }
}
