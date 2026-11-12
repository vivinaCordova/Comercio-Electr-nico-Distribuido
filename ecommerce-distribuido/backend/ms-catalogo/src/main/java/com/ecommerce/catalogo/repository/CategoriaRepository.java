package com.ecommerce.catalogo.repository;

import com.ecommerce.catalogo.dto.CategoriaResponse;
import com.ecommerce.catalogo.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("""
            select new com.ecommerce.catalogo.dto.CategoriaResponse(c.id, c.nombre, c.slug, c.icono, c.descripcion, count(p.id))
            from Categoria c
            left join Producto p on p.categoria = c and p.activo = true
            group by c.id, c.nombre, c.slug, c.icono, c.descripcion
            order by c.id
            """)
    List<CategoriaResponse> listarConConteo();
}
