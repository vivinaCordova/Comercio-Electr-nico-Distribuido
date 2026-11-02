package com.ecommerce.notificaciones.repository;
import com.ecommerce.notificaciones.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NotificacionRepository extends JpaRepository<Notificacion,Long> {}
