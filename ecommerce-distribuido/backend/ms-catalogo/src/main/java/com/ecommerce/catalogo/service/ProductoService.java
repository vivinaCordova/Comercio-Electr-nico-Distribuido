package com.ecommerce.catalogo.service;

import com.ecommerce.catalogo.dto.PageResponse;
import com.ecommerce.catalogo.dto.ProductoCreateRequest;
import com.ecommerce.catalogo.dto.ProductoResponse;
import com.ecommerce.catalogo.dto.ProductoUpdateRequest;
import com.ecommerce.catalogo.entity.Producto;
import com.ecommerce.catalogo.exception.ProductoNotFoundException;
import com.ecommerce.catalogo.exception.SkuDuplicadoException;
import com.ecommerce.catalogo.exception.SolicitudInvalidaException;
import com.ecommerce.catalogo.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ProductoService {

    private static final int TAMANIO_MAXIMO = 100;
    private static final Set<String> CAMPOS_ORDENABLES =
            Set.of("id", "nombre", "precio", "stock", "sku", "fechaCreacion");

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public ProductoService(ProductoRepository productoRepository, CategoriaService categoriaService) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
    }

    @Transactional
    public ProductoResponse crear(ProductoCreateRequest request) {
        String sku = normalizarSku(request.sku());
        if (productoRepository.existsBySku(sku)) {
            throw new SkuDuplicadoException(sku);
        }
        Producto producto = ProductoMapper.toEntity(request, sku);
        producto.setCategoria(categoriaService.resolver(request.categoriaId()));
        Producto guardado = productoRepository.saveAndFlush(producto);
        return ProductoMapper.toResponse(guardado);
    }

    public ProductoResponse obtenerPorId(Long id) {
        return ProductoMapper.toResponse(buscarOFallar(id));
    }

    public PageResponse<ProductoResponse> listar(int pagina, int tamanio, String ordenarPor, String direccion) {
        Pageable pageable = construirPageable(pagina, tamanio, ordenarPor, direccion);
        return PageResponse.from(productoRepository.findAll(pageable).map(ProductoMapper::toResponse));
    }

    // Filtros opcionales y combinables: categoria (slug) y q (texto en el nombre)
    public PageResponse<ProductoResponse> listarActivos(int pagina, int tamanio, String ordenarPor, String direccion,
                                                        String categoria, String q) {
        Pageable pageable = construirPageable(pagina, tamanio, ordenarPor, direccion);
        boolean porCategoria = categoria != null && !categoria.isBlank();
        boolean porTexto = q != null && !q.isBlank();
        String slug = porCategoria ? categoria.trim().toLowerCase(Locale.ROOT) : null;
        String texto = porTexto ? q.trim() : null;

        Page<Producto> resultado;
        if (porCategoria && porTexto) {
            resultado = productoRepository.findByActivoTrueAndCategoriaSlugAndNombreContainingIgnoreCase(slug, texto, pageable);
        } else if (porCategoria) {
            resultado = productoRepository.findByActivoTrueAndCategoriaSlug(slug, pageable);
        } else if (porTexto) {
            resultado = productoRepository.findByActivoTrueAndNombreContainingIgnoreCase(texto, pageable);
        } else {
            resultado = productoRepository.findByActivoTrue(pageable);
        }
        return PageResponse.from(resultado.map(ProductoMapper::toResponse));
    }

    public PageResponse<ProductoResponse> buscarPorNombre(String nombre, int pagina, int tamanio,
                                                          String ordenarPor, String direccion) {
        if (nombre == null || nombre.isBlank()) {
            throw new SolicitudInvalidaException("El parámetro 'nombre' no puede estar vacío");
        }
        Pageable pageable = construirPageable(pagina, tamanio, ordenarPor, direccion);
        return PageResponse.from(
                productoRepository.findByNombreContainingIgnoreCase(nombre.trim(), pageable)
                        .map(ProductoMapper::toResponse));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoUpdateRequest request) {
        Producto producto = buscarOFallar(id);
        String sku = normalizarSku(request.sku());
        if (productoRepository.existsBySkuAndIdNot(sku, id)) {
            throw new SkuDuplicadoException(sku);
        }
        ProductoMapper.aplicarActualizacion(producto, request, sku);
        producto.setCategoria(categoriaService.resolver(request.categoriaId()));
        return ProductoMapper.toResponse(productoRepository.saveAndFlush(producto));
    }

    @Transactional
    public void desactivar(Long id) {
        Producto producto = buscarOFallar(id);
        producto.setActivo(false);
        productoRepository.saveAndFlush(producto);
    }

    private Producto buscarOFallar(Long id) {
        return productoRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
    }

    private String normalizarSku(String sku) {
        return sku.trim().toUpperCase(Locale.ROOT);
    }

    private Pageable construirPageable(int pagina, int tamanio, String ordenarPor, String direccion) {
        if (pagina < 0) {
            throw new SolicitudInvalidaException("El parámetro 'pagina' no puede ser negativo");
        }
        if (tamanio < 1 || tamanio > TAMANIO_MAXIMO) {
            throw new SolicitudInvalidaException("El parámetro 'tamanio' debe estar entre 1 y " + TAMANIO_MAXIMO);
        }
        if (!CAMPOS_ORDENABLES.contains(ordenarPor)) {
            throw new SolicitudInvalidaException(
                    "El parámetro 'ordenarPor' debe ser uno de: " + String.join(", ", CAMPOS_ORDENABLES));
        }
        Sort.Direction orden;
        try {
            orden = Sort.Direction.fromString(direccion);
        } catch (IllegalArgumentException ex) {
            throw new SolicitudInvalidaException("El parámetro 'direccion' debe ser 'asc' o 'desc'");
        }
        return PageRequest.of(pagina, tamanio, Sort.by(orden, ordenarPor));
    }
}
