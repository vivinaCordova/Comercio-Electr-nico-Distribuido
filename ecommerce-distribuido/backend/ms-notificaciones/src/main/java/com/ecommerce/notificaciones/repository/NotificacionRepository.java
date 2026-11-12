package com.ecommerce.notificaciones.repository;
import com.ecommerce.notificaciones.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findTop10ByUserIdOrderByFechaCreacionDesc(Long userId);
    List<Notificacion> findByUserIdAndLeidaFalseOrderByFechaCreacionDesc(Long userId);
    long countByUserIdAndLeidaFalse(Long userId);
    List<Notificacion> findByUserIdAndLeidaFalse(Long userId);
}
