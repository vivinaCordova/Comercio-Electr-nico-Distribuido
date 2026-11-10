package com.ecommerce.ordenes.repository;
import com.ecommerce.ordenes.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.ordenes.entity.EstadoOrden;
import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    Optional<Orden> findByIdAndEstado(Long id, EstadoOrden estado);
}
