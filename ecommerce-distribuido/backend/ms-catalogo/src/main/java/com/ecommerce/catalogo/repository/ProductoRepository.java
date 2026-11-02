package com.ecommerce.catalogo.repository;
import com.ecommerce.catalogo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductoRepository extends JpaRepository<Producto,Long> {}
