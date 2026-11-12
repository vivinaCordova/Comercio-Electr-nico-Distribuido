package com.ecommerce.ordenes;

import com.ecommerce.ordenes.dto.*;
import com.ecommerce.ordenes.repository.OrdenRepository;
import com.ecommerce.ordenes.messaging.EventPublisher;
import com.ecommerce.ordenes.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrdenServiceValidationTest {
    @Mock OrdenRepository repository;
    @Mock CatalogoClient catalogoClient;
    @Mock EventPublisher eventPublisher;

    @Test
    void rechazaPrecioDiferenteYSinPersistir() {
        when(catalogoClient.validar(eq(1L), any())).thenReturn(
                new CatalogoValidationResponse(true, new BigDecimal("20.00"), "ok"));

        OrdenService service = new OrdenService(repository, catalogoClient, eventPublisher);

        assertThrows(OrdenValidationException.class, () -> service.crear(
                new CrearOrdenRequest(10L,
                        List.of(new OrdenItemRequest(1L, 1, new BigDecimal("19.99"))))));
        verify(repository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }
}
