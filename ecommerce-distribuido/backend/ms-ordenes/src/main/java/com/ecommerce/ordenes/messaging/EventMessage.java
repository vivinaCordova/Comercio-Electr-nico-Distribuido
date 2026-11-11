package com.ecommerce.ordenes.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record EventMessage(String tipo, String ordenId, Long userId, BigDecimal total, Instant fecha, List<Item> items) {
    public record Item(Long productoId, Integer cantidad) { }

    // Constructor corto para eventos sin items (los de stock)
    public EventMessage(String tipo, String ordenId, Long userId, BigDecimal total, Instant fecha) {
        this(tipo, ordenId, userId, total, fecha, List.of());
    }
}
