package com.ecommerce.catalogo.controller;

import com.ecommerce.catalogo.dto.CategoriaResponse;
import com.ecommerce.catalogo.service.CategoriaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/catalogo/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Categorías oficiales con la cantidad de productos activos de cada una
    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaService.listarConConteo();
    }
}
