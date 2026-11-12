package com.ecommerce.notificaciones.messaging;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record EventMessage(String tipo, String ordenId, Long userId, BigDecimal total,
                           Instant fecha, List<Item> items, String detalle) {
    public record Item(Long productoId, Integer cantidad) {}
}
