package com.ecommerce.catalogo.repository;

import com.ecommerce.catalogo.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    Page<Producto> findByActivoTrue(Pageable pageable);

    Page<Producto> findByActivoTrueAndCategoriaSlug(String slug, Pageable pageable);

    Page<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<Producto> findByActivoTrueAndCategoriaSlugAndNombreContainingIgnoreCase(String slug, String nombre, Pageable pageable);

    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
