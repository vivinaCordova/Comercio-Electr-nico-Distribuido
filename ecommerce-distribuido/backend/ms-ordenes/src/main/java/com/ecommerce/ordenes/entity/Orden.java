package com.ecommerce.ordenes.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden")
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estado;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(precision = 19, scale = 2)
    private BigDecimal descuento;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    // Solo se llena cuando la orden se cancela (p. ej. "Stock insuficiente")
    private String motivo;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenItem> items = new ArrayList<>();

    protected Orden() {
    }

    public Orden(Long userId, EstadoOrden estado, BigDecimal total) {
        this.userId = userId;
        this.estado = estado;
        this.total = total;
        this.descuento = BigDecimal.ZERO;
        this.fechaCreacion = Instant.now();
    }

    public void agregarItem(OrdenItem item) {
        items.add(item);
        item.asignarOrden(this);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public EstadoOrden getEstado() { return estado; }
    public BigDecimal getTotal() { return total; }
    public BigDecimal getDescuento() { return descuento; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public String getMotivo() { return motivo; }
    public List<OrdenItem> getItems() { return List.copyOf(items); }
    public void cambiarEstado(EstadoOrden estado, String motivo) {
        this.estado = estado;
        this.motivo = motivo;
    }
    public void fijarImportes(BigDecimal descuento, BigDecimal total) {
        this.descuento = descuento;
        this.total = total;
    }
}
