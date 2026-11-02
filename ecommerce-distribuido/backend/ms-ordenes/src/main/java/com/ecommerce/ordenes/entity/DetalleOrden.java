package com.ecommerce.ordenes.entity;
import jakarta.persistence.*;
@Entity public class DetalleOrden { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long productoId; private Integer cantidad; }
