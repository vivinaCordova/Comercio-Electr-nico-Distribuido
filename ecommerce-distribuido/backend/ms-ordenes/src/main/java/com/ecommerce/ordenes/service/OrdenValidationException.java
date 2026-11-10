package com.ecommerce.ordenes.service;

public class OrdenValidationException extends RuntimeException {
    public OrdenValidationException(String message) { super(message); }
    public OrdenValidationException(String message, Throwable cause) { super(message, cause); }
}
