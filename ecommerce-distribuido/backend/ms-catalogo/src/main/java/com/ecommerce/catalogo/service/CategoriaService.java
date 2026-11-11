package com.ecommerce.catalogo.service;

import com.ecommerce.catalogo.dto.CategoriaResponse;
import com.ecommerce.catalogo.entity.Categoria;
import com.ecommerce.catalogo.exception.SolicitudInvalidaException;
import com.ecommerce.catalogo.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponse> listarConConteo() {
        return categoriaRepository.listarConConteo();
    }

    // Un producto puede no tener categoría (null); si se indica una, debe existir
    Categoria resolver(Long categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new SolicitudInvalidaException("La categoría " + categoriaId + " no existe"));
    }
}
