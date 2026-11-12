package com.ecommerce.notificaciones.service;

import com.ecommerce.notificaciones.messaging.EventMessage;

public interface PlantillaNotificacion {
    boolean aplica(String tipo);
    String titulo(EventMessage evento);
    String mensaje(EventMessage evento);
}
