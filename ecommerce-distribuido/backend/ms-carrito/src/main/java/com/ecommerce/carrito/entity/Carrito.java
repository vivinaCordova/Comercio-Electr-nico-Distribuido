package com.ecommerce.carrito.entity;
import jakarta.persistence.*;
@Entity @Table(name="carrito") public class Carrito {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
private String clienteReferencia;
public Long getId() {return id;}
}
