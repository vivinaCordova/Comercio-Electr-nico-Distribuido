package com.ecommerce.notificaciones.service;

import com.ecommerce.notificaciones.messaging.EventMessage;
import org.springframework.stereotype.Component;

@Component
public class PlantillaResultadoStock implements PlantillaNotificacion {
    public boolean aplica(String tipo) {
        return "stock.descontado".equals(tipo) || "stock.insuficiente".equals(tipo);
    }
    public String titulo(EventMessage evento) {
        return "Tu orden #" + evento.ordenId()
                + ("stock.descontado".equals(evento.tipo()) ? " fue confirmada" : " fue cancelada");
    }
    public String mensaje(EventMessage evento) {
        return "stock.descontado".equals(evento.tipo())
                ? "Tu pedido está confirmado y será preparado."
                : "Tu orden fue cancelada: " + (evento.detalle() == null ? "stock insuficiente" : evento.detalle());
    }
}
