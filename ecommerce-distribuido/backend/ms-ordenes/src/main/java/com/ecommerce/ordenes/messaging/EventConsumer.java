package com.ecommerce.ordenes.messaging;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ecommerce.ordenes.entity.EstadoOrden;
import com.ecommerce.ordenes.service.OrdenService;
@Component public class EventConsumer {
 private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);
 private final OrdenService ordenService;
 public EventConsumer(OrdenService ordenService) { this.ordenService = ordenService; }
 @RabbitListener(queues="ordenes.stock-eventos")
 public void recibir(EventMessage evento) {
  log.info("Evento recibido: tipo={}, ordenId={}, userId={}",
      evento.tipo(), evento.ordenId(), evento.userId());
  if ("stock.descontado".equals(evento.tipo())) {
   ordenService.actualizarEstado(Long.valueOf(evento.ordenId()), EstadoOrden.CONFIRMADA, null);
  } else if ("stock.insuficiente".equals(evento.tipo())) {
   ordenService.actualizarEstado(Long.valueOf(evento.ordenId()), EstadoOrden.CANCELADA, evento.detalle());
  } else {
   log.warn("Evento ignorado por tipo no soportado: tipo={}, ordenId={}", evento.tipo(), evento.ordenId());
  }
 }
}
