package com.ecommerce.ordenes.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
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

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenItem> items = new ArrayList<>();

    protected Orden() {
    }

    public Orden(Long userId, EstadoOrden estado, BigDecimal total) {
        this.userId = userId;
        this.estado = estado;
        this.total = total;
    }

    public void agregarItem(OrdenItem item) {
        items.add(item);
        item.asignarOrden(this);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public EstadoOrden getEstado() { return estado; }
    public BigDecimal getTotal() { return total; }
    public List<OrdenItem> getItems() { return List.copyOf(items); }
    public void cambiarEstado(EstadoOrden estado) { this.estado = estado; }
    public void actualizarTotal(BigDecimal total) { this.total = total; }
}
