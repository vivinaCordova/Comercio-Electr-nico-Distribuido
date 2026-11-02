package com.ecommerce.ordenes.entity;
import jakarta.persistence.*;
@Entity @Table(name="orden") public class Orden {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
private String estado;
private java.math.BigDecimal total;
public Long getId() {return id;}
}
