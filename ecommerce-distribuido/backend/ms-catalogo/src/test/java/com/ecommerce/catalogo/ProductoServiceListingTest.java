package com.ecommerce.catalogo;

import com.ecommerce.catalogo.entity.Producto;
import com.ecommerce.catalogo.exception.SolicitudInvalidaException;
import com.ecommerce.catalogo.repository.ProductoRepository;
import com.ecommerce.catalogo.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceListingTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void listarDevuelvePaginaYAplicaOrdenSolicitado() {
        Producto producto = new Producto();
        producto.setNombre("Teclado");
        producto.setPrecio(new BigDecimal("45.90"));
        producto.setStock(12);
        producto.setSku("SKU-101");
        when(productoRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(producto)));

        var pagina = productoService.listar(0, 10, "nombre", "desc");

        assertEquals(1, pagina.contenido().size());
        assertEquals("Teclado", pagina.contenido().get(0).nombre());
        assertEquals(1, pagina.totalElementos());
        verify(productoRepository).findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "nombre")));
    }

    @Test
    void listarRechazaParametrosFueraDeRangoSinConsultarRepositorio() {
        assertThrows(SolicitudInvalidaException.class, () -> productoService.listar(-1, 10, "id", "asc"));
        assertThrows(SolicitudInvalidaException.class, () -> productoService.listar(0, 101, "id", "asc"));
        assertThrows(SolicitudInvalidaException.class, () -> productoService.listar(0, 10, "password", "asc"));
        assertThrows(SolicitudInvalidaException.class, () -> productoService.listar(0, 10, "id", "sideways"));
    }
}
