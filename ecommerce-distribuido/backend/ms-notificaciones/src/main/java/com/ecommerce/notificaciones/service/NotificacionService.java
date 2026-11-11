package com.ecommerce.notificaciones.service;
import com.ecommerce.notificaciones.entity.Notificacion;
import com.ecommerce.notificaciones.messaging.EventMessage;
import com.ecommerce.notificaciones.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificacionService {
    private final NotificacionRepository repository;
    private final List<PlantillaNotificacion> plantillas;

    public NotificacionService(NotificacionRepository repository, List<PlantillaNotificacion> plantillas) {
        this.repository = repository;
        this.plantillas = plantillas;
    }

    @Transactional
    public void procesar(EventMessage evento) {
        if (evento.userId() == null) {
            throw new IllegalArgumentException("El evento no contiene userId: " + evento.tipo());
        }
        PlantillaNotificacion plantilla = plantillas.stream()
                .filter(p -> p.aplica(evento.tipo()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de evento no soportado: " + evento.tipo()));
        repository.save(new Notificacion(evento.userId(), evento.tipo(),
                plantilla.titulo(evento), plantilla.mensaje(evento)));
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listar(Long userId, boolean soloNoLeidas) {
        return soloNoLeidas
                ? repository.findByUserIdAndLeidaFalseOrderByFechaCreacionDesc(userId)
                : repository.findTop10ByUserIdOrderByFechaCreacionDesc(userId);
    }

    @Transactional(readOnly = true)
    public long contarNoLeidas(Long userId) { return repository.countByUserIdAndLeidaFalse(userId); }

    @Transactional
    public void marcarLeida(Long id) {
        repository.findById(id).ifPresent(Notificacion::marcarLeida);
    }

    @Transactional
    public void marcarTodas(Long userId) {
        repository.findByUserIdAndLeidaFalse(userId).forEach(Notificacion::marcarLeida);
    }
}
