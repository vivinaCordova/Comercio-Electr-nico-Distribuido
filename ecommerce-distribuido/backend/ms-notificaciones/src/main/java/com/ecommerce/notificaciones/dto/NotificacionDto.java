package com.ecommerce.notificaciones.dto;

import com.ecommerce.notificaciones.entity.Notificacion;
import java.time.Instant;

public record NotificacionDto(Long id, String tipo, String titulo, String mensaje,
                              boolean leida, Instant fechaCreacion) {
    public static NotificacionDto from(Notificacion n) {
        return new NotificacionDto(n.getId(), n.getTipo(), n.getTitulo(), n.getMensaje(),
                n.isLeida(), n.getFechaCreacion());
    }
}
