
package com.ecommerce.catalogo.exception;

public class ImagenDemasiadoGrandeException extends RuntimeException {

    public ImagenDemasiadoGrandeException() {
        super("La imagen supera el tamaño máximo permitido de 5 MB");
    }
}

