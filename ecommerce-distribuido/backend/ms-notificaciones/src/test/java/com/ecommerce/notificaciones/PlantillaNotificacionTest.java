package com.ecommerce.notificaciones;

import com.ecommerce.notificaciones.messaging.EventMessage;
import com.ecommerce.notificaciones.service.PlantillaResultadoStock;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlantillaNotificacionTest {
    private final PlantillaResultadoStock plantilla = new PlantillaResultadoStock();

    @Test
    void construyeMensajeDeConfirmacion() {
        var evento = new EventMessage("stock.descontado", "4", 7L, null, Instant.now(), List.of(), "Stock descontado");
        assertEquals("Tu orden #4 fue confirmada", plantilla.titulo(evento));
    }

    @Test
    void construyeMensajeDeCancelacion() {
        var evento = new EventMessage("stock.insuficiente", "4", 7L, null, Instant.now(), List.of(), "Stock insuficiente");
        assertEquals("Tu orden #4 fue cancelada", plantilla.titulo(evento));
    }
}
