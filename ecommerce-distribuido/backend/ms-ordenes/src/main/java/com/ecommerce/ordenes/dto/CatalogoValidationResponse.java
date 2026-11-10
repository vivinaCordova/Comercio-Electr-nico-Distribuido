package com.ecommerce.ordenes.dto;

import java.math.BigDecimal;

public record CatalogoValidationResponse(boolean valido, BigDecimal precio, String detalle) {
}
