package com.ecommerce.catalogo.messaging;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Component public class EventConsumer {
 @RabbitListener(queues="catalogo.orden-creada")
 public void recibir(EventMessage evento) { /* TODO: implementar reglas empresariales en una fase futura. */ }
}
