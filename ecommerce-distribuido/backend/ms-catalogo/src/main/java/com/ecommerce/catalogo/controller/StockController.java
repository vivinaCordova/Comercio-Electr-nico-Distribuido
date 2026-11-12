package com.ecommerce.catalogo.controller;

import com.ecommerce.catalogo.dto.StockDtos.ValidacionRequest;
import com.ecommerce.catalogo.dto.StockDtos.ValidacionResponse;
import com.ecommerce.catalogo.service.StockService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogo/productos")
public class StockController {
    private final StockService stock;

    public StockController(StockService stock) { this.stock = stock; }

    @PostMapping("/{id}/validar")
    public ValidacionResponse validar(@PathVariable("id") Long id, @Valid @RequestBody ValidacionRequest request) {
        return stock.validar(id, request);
    }
}
