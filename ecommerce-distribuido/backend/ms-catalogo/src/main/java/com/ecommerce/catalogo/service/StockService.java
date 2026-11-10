package com.ecommerce.catalogo.service;

import com.ecommerce.catalogo.dto.StockDtos.ItemOrden;
import com.ecommerce.catalogo.dto.StockDtos.ValidacionRequest;
import com.ecommerce.catalogo.dto.StockDtos.ValidacionResponse;
import com.ecommerce.catalogo.entity.Producto;
import com.ecommerce.catalogo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockService {
    private final ProductoRepository repo;

    public StockService(ProductoRepository repo) { this.repo = repo; }

    // Consulta síncrona de Órdenes: el producto existe, está activo, hay stock y el precio coincide
    @Transactional(readOnly = true)
    public ValidacionResponse validar(Long id, ValidacionRequest r) {
        Producto p = repo.findById(id).orElse(null);
        if (p == null || !Boolean.TRUE.equals(p.getActivo())) return new ValidacionResponse(false, null, "Producto no disponible");
        if (p.getStock() < r.cantidad()) return new ValidacionResponse(false, p.getPrecio(), "Stock insuficiente");
        if (r.precioEsperado() != null && p.getPrecio().compareTo(r.precioEsperado()) != 0) return new ValidacionResponse(false, p.getPrecio(), "El precio cambió");
        return new ValidacionResponse(true, p.getPrecio(), "Producto válido");
    }

    // Descuenta el stock de todos los productos de la orden, o de ninguno
    @Transactional
    public boolean descontar(List<ItemOrden> items) {
        if (items == null || items.isEmpty()) return false;
        Map<Long, Integer> pedido = items.stream().collect(Collectors.toMap(ItemOrden::productoId, ItemOrden::cantidad, Integer::sum));
        Map<Long, Producto> productos = new HashMap<>();
        for (Map.Entry<Long, Integer> e : pedido.entrySet()) {
            Producto p = repo.findById(e.getKey()).orElse(null);
            if (p == null || p.getStock() < e.getValue()) return false;
            productos.put(e.getKey(), p);
        }
        pedido.forEach((id, cantidad) -> productos.get(id).setStock(productos.get(id).getStock() - cantidad));
        return true;
    }
}
