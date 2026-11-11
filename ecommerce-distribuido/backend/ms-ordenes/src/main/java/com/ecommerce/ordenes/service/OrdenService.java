package com.ecommerce.ordenes.service;

import com.ecommerce.ordenes.dto.*;
import com.ecommerce.ordenes.entity.*;
import com.ecommerce.ordenes.messaging.EventMessage;
import com.ecommerce.ordenes.messaging.EventPublisher;
import com.ecommerce.ordenes.repository.OrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
public class OrdenService {
    static final String PROMO_SELL12 = "SELL12";
    private static final BigDecimal TASA_SELL12 = new BigDecimal("0.12");

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
        BigDecimal subtotal = BigDecimal.ZERO;
        Orden orden = new Orden(request.userId(), EstadoOrden.PENDIENTE, BigDecimal.ZERO);
        for (OrdenItemRequest item : request.items()) {
            CatalogoValidationResponse validation = catalogoClient.validar(item.productoId(),
                    new CatalogoValidationRequest(item.precioEsperado(), item.cantidad(), true));
            if (!validation.valido() || validation.precio() == null
                    || validation.precio().compareTo(item.precioEsperado()) != 0) {
                throw new OrdenValidationException(motivo(validation, item));
            }
            subtotal = subtotal.add(validation.precio().multiply(BigDecimal.valueOf(item.cantidad())));
            orden.agregarItem(new OrdenItem(item.productoId(), item.cantidad(), validation.precio()));
        }
        // El descuento lo calcula el servidor: la factura del frontend debe coincidir con este total
        BigDecimal descuento = descuentoPara(request.promo(), subtotal);
        BigDecimal total = subtotal.subtract(descuento);
        orden.fijarImportes(descuento, total);
        orden = repository.save(orden);
        eventPublisher.publicar("orden.creada",
                new EventMessage("orden.creada", orden.getId().toString(), orden.getUserId(), orden.getTotal(), Instant.now(), orden.getItems().stream().map(i -> new EventMessage.Item(i.getProductoId(), i.getCantidad())).toList()));
        return OrdenResponse.from(orden);
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listarPorUsuario(Long userId) {
        return repository.findByUserIdOrderByFechaCreacionDescIdDesc(userId).stream()
                .map(OrdenResponse::from)
                .toList();
    }

    @Transactional
    public void actualizarEstado(Long ordenId, EstadoOrden estado, String motivo) {
        Orden orden = repository.findById(ordenId)
                .orElseThrow(() -> new OrdenValidationException("Orden no encontrada: " + ordenId));
        orden.cambiarEstado(estado, motivo);
    }

    // Usa el detalle que devuelve ms-catalogo (p. ej. "Stock insuficiente") en vez de un mensaje genérico
    private String motivo(CatalogoValidationResponse validation, OrdenItemRequest item) {
        String detalle = validation.detalle() == null || validation.detalle().isBlank()
                ? "Precio inválido" : validation.detalle();
        return detalle + " (producto " + item.productoId() + ")";
    }

    private BigDecimal descuentoPara(String promo, BigDecimal subtotal) {
        if (promo == null || promo.isBlank()) {
            return BigDecimal.ZERO.setScale(2);
        }
        if (!PROMO_SELL12.equalsIgnoreCase(promo.trim())) {
            throw new OrdenValidationException("Código de promoción no válido: " + promo);
        }
        return subtotal.multiply(TASA_SELL12).setScale(2, RoundingMode.HALF_UP);
    }
}
