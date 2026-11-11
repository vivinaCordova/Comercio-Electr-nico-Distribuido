package com.ecommerce.catalogo;

import com.ecommerce.catalogo.dto.ProductoCreateRequest;
import com.ecommerce.catalogo.exception.SkuDuplicadoException;
import com.ecommerce.catalogo.entity.Producto;
import com.ecommerce.catalogo.repository.ProductoRepository;
import com.ecommerce.catalogo.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceCreateTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crearNormalizaNombreYSkuYActivaElProductoPorDefecto() {
        when(productoRepository.existsBySku("SKU-101")).thenReturn(false);
        when(productoRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(Producto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ProductoCreateRequest request = new ProductoCreateRequest(
                "  Teclado  ", "Mecanico", new BigDecimal("45.90"), 12, " sku-101 ", null);

        productoService.crear(request);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).saveAndFlush(captor.capture());
        assertEquals("Teclado", captor.getValue().getNombre());
        assertEquals("SKU-101", captor.getValue().getSku());
        assertEquals(Boolean.TRUE, captor.getValue().getActivo());
        assertEquals(new BigDecimal("45.90"), captor.getValue().getPrecio());
        assertEquals(12, captor.getValue().getStock());
    }

    @Test
    void crearRechazaSkuDuplicadoSinGuardar() {
        when(productoRepository.existsBySku("SKU-101")).thenReturn(true);
        ProductoCreateRequest request = new ProductoCreateRequest(
                "Teclado", null, new BigDecimal("45.90"), 12, "sku-101", null);

        assertThrows(SkuDuplicadoException.class, () -> productoService.crear(request));

        verify(productoRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }
}
