package com.ecommerce.notificaciones;

import com.ecommerce.notificaciones.entity.Notificacion;
import com.ecommerce.notificaciones.messaging.EventMessage;
import com.ecommerce.notificaciones.repository.NotificacionRepository;
import com.ecommerce.notificaciones.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificacionServiceTest {
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
