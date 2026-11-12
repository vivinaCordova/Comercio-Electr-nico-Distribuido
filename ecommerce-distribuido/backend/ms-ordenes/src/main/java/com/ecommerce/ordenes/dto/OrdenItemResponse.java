package com.ecommerce.ordenes.dto;

import java.math.BigDecimal;

public record OrdenItemResponse(Long productoId, Integer cantidad, BigDecimal precioUnitario) {
}
