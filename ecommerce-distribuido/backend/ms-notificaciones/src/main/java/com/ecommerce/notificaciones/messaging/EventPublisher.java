package com.ecommerce.notificaciones.messaging;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
@Component public class EventPublisher {
 private final RabbitTemplate template;
 public EventPublisher(RabbitTemplate template) {this.template=template;}
 public void publicar(String routingKey, EventMessage evento) {template.convertAndSend("ecommerce.events", routingKey, evento);}
}
