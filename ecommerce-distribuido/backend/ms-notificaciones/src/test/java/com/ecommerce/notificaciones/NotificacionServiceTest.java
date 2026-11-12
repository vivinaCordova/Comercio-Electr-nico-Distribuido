package com.ecommerce.notificaciones;

import com.ecommerce.notificaciones.entity.Notificacion;
import com.ecommerce.notificaciones.messaging.EventMessage;
import com.ecommerce.notificaciones.repository.NotificacionRepository;
import com.ecommerce.notificaciones.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class NotificacionServiceTest {
    @ParameterizedTest
    @ValueSource(strings = {"orden.creada", "stock.descontado", "stock.insuficiente"})
    void descartaEventoSinUsuarioSinProvocarReencola(String tipo) {
        var repository = mock(NotificacionRepository.class);
        var service = new NotificacionService(repository,
                List.of(new PlantillaOrdenCreada(), new PlantillaResultadoStock()));
        var evento = new EventMessage(tipo, "9", null, null, Instant.now(), List.of(), null);

        assertDoesNotThrow(() -> service.procesar(evento));

        verifyNoInteractions(repository);
    }

    @Test
    void descartaEventoNuloOTipoNoSoportado() {
        var repository = mock(NotificacionRepository.class);
        var service = new NotificacionService(repository,
                List.of(new PlantillaOrdenCreada(), new PlantillaResultadoStock()));

        assertDoesNotThrow(() -> service.procesar(null));
        assertDoesNotThrow(() -> service.procesar(new EventMessage(
                "evento.desconocido", "9", 12L, null, Instant.now(), List.of(), null)));

        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"stock.descontado", "stock.insuficiente"})
    void guardaResultadoStockConUsuario(String tipo) {
        var repository = mock(NotificacionRepository.class);
        var service = new NotificacionService(repository, List.of(new PlantillaResultadoStock()));

        service.procesar(new EventMessage(tipo, "9", 12L, null, Instant.now(), List.of(), null));

        var captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(repository).save(captor.capture());
        assertEquals(12L, captor.getValue().getUserId());
        assertEquals(tipo, captor.getValue().getTipo());
    }

    @Test
    void procesaEventoYGuardaNotificacion() {
        var repository = mock(NotificacionRepository.class);
        var service = new NotificacionService(repository,
                List.of(new PlantillaOrdenCreada(), new PlantillaResultadoStock()));
        var evento = new EventMessage("orden.creada", "9", 12L, null, Instant.now(), List.of(), null);

        service.procesar(evento);

        var captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(repository).save(captor.capture());
        assertEquals(12L, captor.getValue().getUserId());
        assertEquals("Orden #9 creada", captor.getValue().getTitulo());
    }
}
