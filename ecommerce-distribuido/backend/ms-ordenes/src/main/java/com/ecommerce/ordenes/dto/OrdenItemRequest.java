package com.ecommerce.ordenes.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrdenItemRequest(
        @JsonAlias({"productId", "productoId"}) @NotNull Long productoId,
        @JsonAlias({"quantity", "cantidad"}) @NotNull @Min(1) Integer cantidad,
        @NotNull @DecimalMin(value = "0.00") BigDecimal precioEsperado) {
}
