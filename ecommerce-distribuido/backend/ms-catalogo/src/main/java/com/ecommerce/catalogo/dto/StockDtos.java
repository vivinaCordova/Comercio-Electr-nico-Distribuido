package com.ecommerce.catalogo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public final class StockDtos {
    private StockDtos() { }

    public record ValidacionRequest(BigDecimal precioEsperado, @NotNull @Min(1) Integer cantidad, Boolean simularPago) { }

    public record ValidacionResponse(boolean valido, BigDecimal precio, String detalle) { }

    // Lo que Órdenes publica en el evento orden.creada
    public record ItemOrden(Long productoId, Integer cantidad) { }

    public record OrdenCreada(String ordenId, Long userId, List<ItemOrden> items) { }
}
