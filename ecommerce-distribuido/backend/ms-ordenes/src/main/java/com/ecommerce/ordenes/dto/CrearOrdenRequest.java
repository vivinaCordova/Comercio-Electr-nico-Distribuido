package com.ecommerce.ordenes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CrearOrdenRequest(
        @NotNull Long userId,
        @NotEmpty List<@Valid OrdenItemRequest> items) {
}
