package com.ecommerce.ordenes.dto;

import com.ecommerce.ordenes.entity.Orden;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrdenResponse(Long id, Long userId, String estado, BigDecimal descuento, BigDecimal total,
                            Instant fecha, String motivo, List<OrdenItemResponse> items) {
    public static OrdenResponse from(Orden orden) {
        return new OrdenResponse(orden.getId(), orden.getUserId(), orden.getEstado().name(),
                orden.getDescuento(), orden.getTotal(), orden.getFechaCreacion(), orden.getMotivo(),
                orden.getItems().stream()
                        .map(item -> new OrdenItemResponse(item.getProductoId(), item.getCantidad(),
                                item.getPrecioUnitario()))
                        .toList());
    }
}
