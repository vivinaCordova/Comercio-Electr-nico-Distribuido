package com.ecommerce.ordenes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrdenItemRequest(
        @NotNull Long productoId,
        @NotNull @Min(1) Integer cantidad,
        @NotNull @DecimalMin(value = "0.00") BigDecimal precioEsperado) {
}
