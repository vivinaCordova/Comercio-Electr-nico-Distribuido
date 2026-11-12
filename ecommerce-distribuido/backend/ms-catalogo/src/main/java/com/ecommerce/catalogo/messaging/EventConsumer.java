package com.ecommerce.catalogo.messaging;

import com.ecommerce.catalogo.dto.StockDtos.OrdenCreada;
import com.ecommerce.catalogo.service.StockService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class EventConsumer {
    private final StockService stock;
    private final EventPublisher publisher;

    public EventConsumer(StockService stock, EventPublisher publisher) {
        this.stock = stock;
        this.publisher = publisher;
    }

    // orden.creada -> descuenta stock -> publica stock.descontado o stock.insuficiente
    @RabbitListener(queues = "catalogo.orden-creada")
    public void recibir(OrdenCreada orden) {
        boolean ok = stock.descontar(orden.items());
        String tipo = ok ? "stock.descontado" : "stock.insuficiente";
        publisher.publicar(tipo, new EventMessage(tipo, orden.ordenId(), orden.userId(),
                ok ? "Stock descontado" : "Stock insuficiente", Instant.now()));
    }
}
