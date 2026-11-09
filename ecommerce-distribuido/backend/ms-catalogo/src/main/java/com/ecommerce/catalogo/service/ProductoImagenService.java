
package com.ecommerce.catalogo.service;

import com.ecommerce.catalogo.dto.ProductoImagenResponse;
import com.ecommerce.catalogo.entity.Producto;
import com.ecommerce.catalogo.entity.ProductoImagen;
import com.ecommerce.catalogo.exception.ImagenNotFoundException;
import com.ecommerce.catalogo.exception.ProductoNotFoundException;
import com.ecommerce.catalogo.repository.ProductoImagenRepository;
import com.ecommerce.catalogo.repository.ProductoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductoImagenService {

    private static final Logger log =
            LoggerFactory.getLogger(ProductoImagenService.class);

    private final ProductoImagenRepository imagenRepository;
    private final ProductoRepository productoRepository;
    private final ImagenStorageService storageService;

    public ProductoImagenService(
            ProductoImagenRepository imagenRepository,
            ProductoRepository productoRepository,
            ImagenStorageService storageService) {

        this.imagenRepository = imagenRepository;
        this.productoRepository = productoRepository;
        this.storageService = storageService;
    }

    // Listar las imagenes de un producto
    public List<ProductoImagenResponse> listar(Long productoId) {

        if (!productoRepository.existsById(productoId)) {
            throw new ProductoNotFoundException(productoId);
        }

        return imagenRepository
                .findByProductoIdOrderByOrdenAsc(productoId)
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    // Subir una imagen y registrarla en PostgreSQL
    @Transactional
    public ProductoImagenResponse subir(
            Long productoId,
            MultipartFile archivo) throws IOException {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(
                        () -> new ProductoNotFoundException(productoId)
                );

        long cantidadImagenes =
                imagenRepository.countByProductoId(productoId);

        boolean primeraImagen = cantidadImagenes == 0;

        String nombreArchivo = storageService.guardar(archivo);

        ProductoImagen imagen = new ProductoImagen();
        imagen.setProducto(producto);
        imagen.setRutaImagen(nombreArchivo);
        imagen.setEsPrincipal(primeraImagen);
        imagen.setOrden(Math.toIntExact(cantidadImagenes));

        try {
            ProductoImagen guardada =
                    imagenRepository.saveAndFlush(imagen);

            return convertirRespuesta(guardada);

        } catch (RuntimeException ex) {

            try {
                storageService.eliminar(nombreArchivo);
            } catch (IOException cleanupError) {
                ex.addSuppressed(cleanupError);
            }

            throw ex;
        }
    }

    // Obtener una imagen para visualizarla en el navegador
    public Resource obtenerArchivo(
            Long productoId,
            Long imagenId) throws IOException {

        ProductoImagen imagen = imagenRepository
                .findByIdAndProductoId(imagenId, productoId)
                .orElseThrow(() ->
                        new ImagenNotFoundException(productoId, imagenId));

        Path ruta = storageService.obtenerRuta(
                imagen.getRutaImagen()
        );

        if (!Files.isRegularFile(ruta) || !Files.isReadable(ruta)) {
            throw new IOException(
                    "El archivo de imagen no esta disponible"
            );
        }

        return new UrlResource(ruta.toUri());
    }

    // Eliminar una imagen y reasignar la principal
    @Transactional
    public void eliminar(Long productoId, Long imagenId) {

        ProductoImagen imagen = imagenRepository
                .findByIdAndProductoId(imagenId, productoId)
                .orElseThrow(() ->
                        new ImagenNotFoundException(productoId, imagenId));

        boolean eraPrincipal =
                Boolean.TRUE.equals(imagen.getEsPrincipal());

        String nombreArchivo = imagen.getRutaImagen();

        // Eliminar el registro de PostgreSQL
        imagenRepository.delete(imagen);
        imagenRepository.flush();

        // Asignar una nueva imagen principal si es necesario
        if (eraPrincipal) {

            List<ProductoImagen> restantes = imagenRepository
                    .findByProductoIdOrderByOrdenAsc(productoId);

            if (!restantes.isEmpty()) {

                ProductoImagen nuevaPrincipal = restantes.get(0);
                nuevaPrincipal.setEsPrincipal(true);

                imagenRepository.saveAndFlush(nuevaPrincipal);
            }
        }

        // Eliminar el archivo fisico solo despues del commit
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {

                        try {
                            storageService.eliminar(nombreArchivo);

                        } catch (IOException ex) {

                            log.error(
                                    "No se pudo eliminar el archivo: {}",
                                    nombreArchivo,
                                    ex
                            );
                        }
                    }
                }
        );
    }

    // Seleccionar manualmente la imagen principal
    @Transactional
    public ProductoImagenResponse establecerPrincipal(
            Long productoId,
            Long imagenId) {

        if (!productoRepository.existsById(productoId)) {
            throw new ProductoNotFoundException(productoId);
        }

        ProductoImagen seleccionada = imagenRepository
                .findByIdAndProductoId(imagenId, productoId)
                .orElseThrow(() ->
                        new ImagenNotFoundException(productoId, imagenId));

        // Si ya es principal, no modificar nada
        if (Boolean.TRUE.equals(seleccionada.getEsPrincipal())) {
            return convertirRespuesta(seleccionada);
        }

        // Quitar la marca de principal de la imagen anterior
        imagenRepository
                .findByProductoIdAndEsPrincipalTrue(productoId)
                .ifPresent(anterior -> {
                    anterior.setEsPrincipal(false);
                    imagenRepository.saveAndFlush(anterior);
                });

        // Establecer la nueva imagen principal
        seleccionada.setEsPrincipal(true);

        return convertirRespuesta(
                imagenRepository.saveAndFlush(seleccionada)
        );
    }

    // Convertir la entidad en una respuesta REST
    private ProductoImagenResponse convertirRespuesta(
            ProductoImagen imagen) {

        String url = "/api/catalogo/productos/"
                + imagen.getProducto().getId()
                + "/imagenes/"
                + imagen.getId()
                + "/archivo";

        return new ProductoImagenResponse(
                imagen.getId(),
                imagen.getProducto().getId(),
                url,
                imagen.getEsPrincipal(),
                imagen.getOrden()
        );
    }
}
