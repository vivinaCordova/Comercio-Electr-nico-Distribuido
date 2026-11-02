package com.ecommerce.catalogo.entity;
import jakarta.persistence.*;
@Entity @Table(name="producto") public class Producto {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
private String nombre;
private java.math.BigDecimal precio;
private Integer stock;
public Long getId() {return id;}
}
