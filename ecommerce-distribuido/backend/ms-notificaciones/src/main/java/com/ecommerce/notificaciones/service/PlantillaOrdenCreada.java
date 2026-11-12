package com.ecommerce.notificaciones.service;

import com.ecommerce.notificaciones.messaging.EventMessage;
import org.springframework.stereotype.Component;

@Component
public class PlantillaOrdenCreada implements PlantillaNotificacion {
    public boolean aplica(String tipo) { return "orden.creada".equals(tipo); }
    public String titulo(EventMessage evento) { return "Orden #" + evento.ordenId() + " creada"; }
    public String mensaje(EventMessage evento) { return "Recibimos tu orden y estamos validando el stock."; }
}
