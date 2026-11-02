package com.ecommerce.notificaciones.messaging;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Component public class EventConsumer {
 @RabbitListener(queues="notificaciones.stock-eventos")
 public void recibir(EventMessage evento) { /* TODO: implementar reglas empresariales en una fase futura. */ }
}
