package com.ecommerce.ordenes.service;

import com.ecommerce.ordenes.dto.*;
import com.ecommerce.ordenes.entity.*;
import com.ecommerce.ordenes.messaging.EventMessage;
import com.ecommerce.ordenes.messaging.EventPublisher;
import com.ecommerce.ordenes.repository.OrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;

@Service
public class OrdenService {
    private final OrdenRepository repository;
    private final CatalogoClient catalogoClient;
    private final EventPublisher eventPublisher;

    public OrdenService(OrdenRepository repository, CatalogoClient catalogoClient,
                        EventPublisher eventPublisher) {
        this.repository = repository;
        this.catalogoClient = catalogoClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public OrdenResponse crear(CrearOrdenRequest request) {
        Orden orden = new Orden(request.userId(), EstadoOrden.PENDIENTE, BigDecimal.ZERO);
        BigDecimal total = BigDecimal.ZERO;
        for (OrdenItemRequest item : request.items()) {
            CatalogoValidationResponse validation = catalogoClient.validar(item.productoId(),
                    new CatalogoValidationRequest(item.precioEsperado(), item.cantidad(), true));
            if (!validation.valido() || validation.precio() == null
                    || validation.precio().compareTo(item.precioEsperado()) != 0) {
                throw new OrdenValidationException(motivo(validation, item));
            }
            total = total.add(validation.precio().multiply(BigDecimal.valueOf(item.cantidad())));
            orden.agregarItem(new OrdenItem(item.productoId(), item.cantidad(), validation.precio()));
        }
        orden.actualizarTotal(total);
        orden = repository.save(orden);
        eventPublisher.publicar("orden.creada",
                new EventMessage("orden.creada", orden.getId().toString(), orden.getUserId(), orden.getTotal(), Instant.now(), orden.getItems().stream().map(i -> new EventMessage.Item(i.getProductoId(), i.getCantidad())).toList()));
        return OrdenResponse.from(orden);
    }

    @Transactional
    public void actualizarEstado(Long ordenId, EstadoOrden estado) {
        Orden orden = repository.findById(ordenId)
                .orElseThrow(() -> new OrdenValidationException("Orden no encontrada: " + ordenId));
        orden.cambiarEstado(estado);
    }

    // Usa el detalle que devuelve ms-catalogo (p. ej. "Stock insuficiente") en vez de un mensaje genérico
    private String motivo(CatalogoValidationResponse validation, OrdenItemRequest item) {
        String detalle = validation.detalle() == null || validation.detalle().isBlank()
                ? "Precio inválido" : validation.detalle();
        return detalle + " (producto " + item.productoId() + ")";
    }
}
