package com.ecommerce.ordenes.messaging;
import java.time.Instant;
import java.math.BigDecimal;
public record EventMessage(String tipo, String ordenId, Long userId, BigDecimal total, Instant fecha) {}
