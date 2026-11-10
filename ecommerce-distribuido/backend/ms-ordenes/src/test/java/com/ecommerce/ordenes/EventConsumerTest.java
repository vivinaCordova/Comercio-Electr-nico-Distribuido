package com.ecommerce.ordenes;

import com.ecommerce.ordenes.entity.EstadoOrden;
import com.ecommerce.ordenes.messaging.*;
import com.ecommerce.ordenes.service.OrdenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class EventConsumerTest {
    @Mock OrdenService ordenService;

    @Test
    void stockDescontadoConfirmaOrden() {
        EventConsumer consumer = new EventConsumer(ordenService);

        consumer.recibir(new EventMessage("stock.descontado", "7", 10L,
                new BigDecimal("39.98"), Instant.now()));

        verify(ordenService).actualizarEstado(7L, EstadoOrden.CONFIRMADA, null);
    }

    @Test
    void stockInsuficienteCancelaOrdenConMotivo() {
        EventConsumer consumer = new EventConsumer(ordenService);

        consumer.recibir(new EventMessage("stock.insuficiente", "8", 10L,
                new BigDecimal("39.98"), Instant.now(), List.of(), "Stock insuficiente"));

        verify(ordenService).actualizarEstado(8L, EstadoOrden.CANCELADA, "Stock insuficiente");
    }
}
