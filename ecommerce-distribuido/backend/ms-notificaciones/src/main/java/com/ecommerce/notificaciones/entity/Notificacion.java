package com.ecommerce.notificaciones.entity;
import jakarta.persistence.*;
@Entity @Table(name="notificacion") public class Notificacion {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
private String destinatario;
private String estado;
public Long getId() {return id;}
}
