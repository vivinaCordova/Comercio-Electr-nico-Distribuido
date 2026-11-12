
package com.ecommerce.catalogo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import com.ecommerce.catalogo.exception.ImagenDemasiadoGrandeException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.util.Iterator;
@Service
public class ImagenStorageService {

    private static final long TAMANIO_MAXIMO = 5L * 1024 * 1024;

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("image/jpeg", "image/png", "image/webp");

    private final Path directorio;

    public ImagenStorageService(
            @Value("${app.imagenes.directorio:uploads/productos}")
            String rutaDirectorio) throws IOException {

        this.directorio = Path.of(rutaDirectorio)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(directorio);
    }

    public String guardar(MultipartFile archivo) throws IOException {

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "La imagen no puede estar vacia");
        }

        if (archivo.getSize() > TAMANIO_MAXIMO) {
    throw new ImagenDemasiadoGrandeException();
}

        String tipo = archivo.getContentType();

        if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo)) {
            throw new IllegalArgumentException(
                    "Solo se permiten imagenes JPG, PNG y WebP");
        }

        byte[] firma;

        try (InputStream entrada = archivo.getInputStream()) {
            firma = entrada.readNBytes(12);
        }

        if (!imagenDecodificable(archivo)) {
    throw new IllegalArgumentException(
            "El archivo no contiene una imagen valida o esta danado");
}

        String extension = switch (tipo) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException(
                    "Tipo de imagen no permitido");
        };

        String nombreArchivo = UUID.randomUUID() + extension;

        Path destino = directorio
                .resolve(nombreArchivo)
                .normalize();

        try (InputStream entrada = archivo.getInputStream()) {
            Files.copy(entrada, destino);
        }

        return nombreArchivo;
    }

    private boolean firmaValida(String tipo, byte[] datos) {

        return switch (tipo) {

            case "image/jpeg" ->
                    datos.length >= 3
                    && (datos[0] & 0xFF) == 0xFF
                    && (datos[1] & 0xFF) == 0xD8
                    && (datos[2] & 0xFF) == 0xFF;

            case "image/png" ->
                    datos.length >= 8
                    && Arrays.equals(
                            Arrays.copyOf(datos, 8),
                            new byte[]{
                                    (byte) 137, 80, 78, 71,
                                    13, 10, 26, 10
                            }
                    );

            case "image/webp" ->
                    datos.length >= 12
                    && Arrays.equals(
                            Arrays.copyOfRange(datos, 0, 4),
                            new byte[]{82, 73, 70, 70}
                    )
                    && Arrays.equals(
                            Arrays.copyOfRange(datos, 8, 12),
                            new byte[]{87, 69, 66, 80}
                    );

            default -> false;
        };
    }

private boolean imagenDecodificable(MultipartFile archivo)
        throws IOException {

    try (InputStream entrada = archivo.getInputStream();
         ImageInputStream flujo = ImageIO.createImageInputStream(entrada)) {

        if (flujo == null) {
            return false;
        }

        Iterator<ImageReader> lectores =
                ImageIO.getImageReaders(flujo);

        if (!lectores.hasNext()) {
            return false;
        }

        ImageReader lector = lectores.next();

        try {
            lector.setInput(flujo);

            return lector.read(0) != null;

        } catch (IOException | RuntimeException ex) {
            return false;

        } finally {
            lector.dispose();
        }
    }
}

    public Path obtenerRuta(String nombreArchivo) {

        Path ruta = directorio
                .resolve(nombreArchivo)
                .normalize();

        if (!ruta.getParent().equals(directorio)) {
            throw new IllegalArgumentException(
                    "Ruta de imagen invalida");
        }

        return ruta;
    }

    public void eliminar(String nombreArchivo) throws IOException {
        Files.deleteIfExists(obtenerRuta(nombreArchivo));
    }
}
