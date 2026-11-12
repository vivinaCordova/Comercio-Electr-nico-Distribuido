package com.ecommerce.catalogo.exception;

public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(Long id) {
        super("No existe un producto con id " + id);
    }
}
