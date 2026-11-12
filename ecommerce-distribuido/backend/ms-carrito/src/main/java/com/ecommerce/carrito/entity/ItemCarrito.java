package com.ecommerce.carrito.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "item_carrito",
       uniqueConstraints = @UniqueConstraint(columnNames = {"carrito_id", "producto_id"}))
public class ItemCarrito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private Integer cantidad;

    protected ItemCarrito() {}

    ItemCarrito(Carrito carrito, Long productoId, int cantidad) {
        this.carrito = carrito;
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    void sumar(int n) { this.cantidad += n; }
    void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public Long getProductoId() { return productoId; }
    public Integer getCantidad() { return cantidad; }
}
