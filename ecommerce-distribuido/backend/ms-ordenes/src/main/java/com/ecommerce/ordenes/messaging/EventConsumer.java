package com.ecommerce.ordenes.messaging;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.ecommerce.ordenes.entity.EstadoOrden;
import com.ecommerce.ordenes.service.OrdenService;
@Component public class EventConsumer {
 private final OrdenService ordenService;
 public EventConsumer(OrdenService ordenService) { this.ordenService = ordenService; }
 @RabbitListener(queues="ordenes.stock-eventos")
 public void recibir(EventMessage evento) {
  if ("stock.descontado".equals(evento.tipo())) {
   ordenService.actualizarEstado(Long.valueOf(evento.ordenId()), EstadoOrden.CONFIRMADA, null);
  } else if ("stock.insuficiente".equals(evento.tipo())) {
   ordenService.actualizarEstado(Long.valueOf(evento.ordenId()), EstadoOrden.CANCELADA, evento.detalle());
  }
 }
}
