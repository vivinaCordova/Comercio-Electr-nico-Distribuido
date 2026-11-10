package com.ecommerce.carrito.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemRequest(@NotNull Long productoId, @NotNull @Min(1) Integer cantidad) {}
