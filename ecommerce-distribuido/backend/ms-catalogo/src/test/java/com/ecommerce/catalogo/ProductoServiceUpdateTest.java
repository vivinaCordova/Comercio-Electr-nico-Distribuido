package com.ecommerce.catalogo;

import com.ecommerce.catalogo.dto.ProductoUpdateRequest;
import com.ecommerce.catalogo.entity.Producto;
import com.ecommerce.catalogo.exception.ProductoNotFoundException;
import com.ecommerce.catalogo.repository.ProductoRepository;
import com.ecommerce.catalogo.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceUpdateTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void actualizarPersisteLosCambiosYConservaActivoSiNoSeIndica() {
        Producto producto = new Producto();
        producto.setNombre("Teclado");
        producto.setPrecio(new BigDecimal("45.90"));
        producto.setStock(12);
        producto.setSku("SKU-101");
        producto.setActivo(false);
        ReflectionTestUtils.setField(producto, "id", 7L);
        when(productoRepository.findById(7L)).thenReturn(Optional.of(producto));
        when(productoRepository.existsBySkuAndIdNot("SKU-102", 7L)).thenReturn(false);
        when(productoRepository.saveAndFlush(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ProductoUpdateRequest request = new ProductoUpdateRequest(
                "  Teclado Pro  ", "Mecanico", new BigDecimal("59.90"), 8, " sku-102 ", null);

        var actualizado = productoService.actualizar(7L, request);

        assertEquals(7L, actualizado.id());
        assertEquals("Teclado Pro", actualizado.nombre());
        assertEquals("SKU-102", actualizado.sku());
        assertEquals(Boolean.FALSE, actualizado.activo());
        assertEquals(new BigDecimal("59.90"), actualizado.precio());
        assertEquals(8, actualizado.stock());
        verify(productoRepository).saveAndFlush(producto);
    }

    @Test
    void actualizarFallaCuandoElProductoNoExiste() {
        when(productoRepository.findById(404L)).thenReturn(Optional.empty());
        ProductoUpdateRequest request = new ProductoUpdateRequest(
                "Teclado", null, new BigDecimal("45.90"), 12, "sku-101", true);

        assertThrows(ProductoNotFoundException.class, () -> productoService.actualizar(404L, request));
    }
}
