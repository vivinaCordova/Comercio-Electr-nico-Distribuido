package com.ecommerce.ordenes.dto;

import java.math.BigDecimal;

public record CatalogoValidationRequest(BigDecimal precioEsperado, Integer cantidad, boolean simularPago) {
}
