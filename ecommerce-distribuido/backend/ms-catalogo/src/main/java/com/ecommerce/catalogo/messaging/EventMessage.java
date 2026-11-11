package com.ecommerce.catalogo.messaging;
import java.time.Instant;
public record EventMessage(String tipo, String ordenId, Long userId, String detalle, Instant fecha) {}
