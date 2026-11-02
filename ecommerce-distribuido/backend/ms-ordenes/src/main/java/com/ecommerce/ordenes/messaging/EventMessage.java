package com.ecommerce.ordenes.messaging;
import java.time.Instant;
public record EventMessage(String tipo, String ordenId, String detalle, Instant fecha) {}
