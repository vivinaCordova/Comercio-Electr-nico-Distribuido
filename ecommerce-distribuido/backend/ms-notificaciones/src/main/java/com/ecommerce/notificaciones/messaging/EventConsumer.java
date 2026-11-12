package com.ecommerce.notificaciones.messaging;
import com.ecommerce.notificaciones.service.NotificacionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {
    private final NotificacionService service;
    public EventConsumer(NotificacionService service) { this.service = service; }

    @RabbitListener(queues = "notificaciones.stock-eventos")
    public void recibirResultado(EventMessage evento) { service.procesar(evento); }

    @RabbitListener(queues = "notificaciones.orden-creada")
    public void recibirCreada(EventMessage evento) { service.procesar(evento); }
}
