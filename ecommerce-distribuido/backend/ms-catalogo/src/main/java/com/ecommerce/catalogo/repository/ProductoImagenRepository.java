
package com.ecommerce.catalogo.repository;

import com.ecommerce.catalogo.entity.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoImagenRepository
        extends JpaRepository<ProductoImagen, Long> {

    List<ProductoImagen> findByProductoIdOrderByOrdenAsc(Long productoId);

    Optional<ProductoImagen> findByIdAndProductoId(
            Long imagenId,
            Long productoId
    );

    Optional<ProductoImagen> findByProductoIdAndEsPrincipalTrue(
            Long productoId
    );

    long countByProductoId(Long productoId);
}
