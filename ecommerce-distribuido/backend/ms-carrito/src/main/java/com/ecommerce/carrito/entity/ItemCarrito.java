package com.ecommerce.carrito.entity;
import jakarta.persistence.*;
@Entity public class ItemCarrito { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long productoId; private Integer cantidad; }
