
package com.ecommerce.catalogo.exception;

public class ImagenNotFoundException extends RuntimeException {

    public ImagenNotFoundException(Long productoId, Long imagenId) {
        super(
            "La imagen con ID " + imagenId
            + " no existe para el producto con ID "
            + productoId
        );
    }
}
