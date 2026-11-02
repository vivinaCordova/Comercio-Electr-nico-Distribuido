package com.ecommerce.ordenes.repository;
import com.ecommerce.ordenes.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrdenRepository extends JpaRepository<Orden,Long> {}
