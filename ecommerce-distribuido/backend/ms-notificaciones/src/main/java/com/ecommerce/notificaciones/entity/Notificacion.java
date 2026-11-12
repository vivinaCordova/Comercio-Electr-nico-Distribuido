package com.ecommerce.notificaciones.entity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(nullable = false, length = 40)
    private String tipo;
    @Column(nullable = false, length = 120)
    private String titulo;
    @Column(nullable = false, length = 500)
    private String mensaje;
    @Column(nullable = false)
    private boolean leida;
    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    protected Notificacion() {}

    public Notificacion(Long userId, String tipo, String titulo, String mensaje) {
        this.userId = userId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaCreacion = Instant.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public boolean isLeida() { return leida; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public void marcarLeida() { leida = true; }
}
