package com.ecommerce.ordenes;

import com.ecommerce.ordenes.dto.*;
import com.ecommerce.ordenes.entity.EstadoOrden;
import com.ecommerce.ordenes.entity.Orden;
import com.ecommerce.ordenes.messaging.EventPublisher;
import com.ecommerce.ordenes.repository.OrdenRepository;
import com.ecommerce.ordenes.service.CatalogoClient;
import com.ecommerce.ordenes.service.OrdenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrdenServiceHappyPathTest {
    @Mock OrdenRepository repository;
    @Mock CatalogoClient catalogoClient;
    @Mock EventPublisher eventPublisher;

    @Test
    void creaOrdenPendientePersisteTotalYPublicaEvento() throws Exception {
        when(catalogoClient.validar(eq(1L), any())).thenReturn(
                new CatalogoValidationResponse(true, new BigDecimal("19.99"), "ok"));
        when(repository.save(any(Orden.class))).thenAnswer(invocation -> {
            Orden orden = invocation.getArgument(0);
            Field id = Orden.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(orden, 7L);
            return orden;
        });

        OrdenResponse response = new OrdenService(repository, catalogoClient, eventPublisher)
                .crear(new CrearOrdenRequest(10L,
                        List.of(new OrdenItemRequest(1L, 2, new BigDecimal("19.99")))));

        assertEquals(7L, response.id());
        assertEquals("PENDIENTE", response.estado());
        assertEquals(new BigDecimal("39.98"), response.total());
        verify(repository).save(any(Orden.class));
        verify(eventPublisher).publicar(eq("orden.creada"), any());
    }
}
