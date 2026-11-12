package com.ecommerce.ordenes.messaging;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component public class EventPublisher {
 private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);
 private final RabbitTemplate template;
 public EventPublisher(RabbitTemplate template) {this.template=template;}
 public void publicar(String routingKey, EventMessage evento) {
  log.info("Publicando evento: tipo={}, ordenId={}, exchange={}, routingKey={}",
      evento.tipo(), evento.ordenId(), "ecommerce.events", routingKey);
  template.convertAndSend("ecommerce.events", routingKey, evento);
 }
}
