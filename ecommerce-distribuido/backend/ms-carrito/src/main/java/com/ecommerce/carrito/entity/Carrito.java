package com.ecommerce.carrito.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long usuarioId;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();

    protected Carrito() {}

    // Factory Method
    public static Carrito paraUsuario(Long usuarioId) {
        Carrito c = new Carrito();
        c.usuarioId = usuarioId;
        return c;
    }

    public void agregar(Long productoId, int cantidad) {
        items.stream().filter(i -> i.getProductoId().equals(productoId)).findFirst()
             .ifPresentOrElse(i -> i.sumar(cantidad),
                              () -> items.add(new ItemCarrito(this, productoId, cantidad)));
    }

    public boolean cambiarCantidad(Long productoId, int cantidad) {
        return items.stream().filter(i -> i.getProductoId().equals(productoId)).findFirst()
                    .map(i -> { i.setCantidad(cantidad); return true; }).orElse(false);
    }

    public boolean quitar(Long productoId) {
        return items.removeIf(i -> i.getProductoId().equals(productoId));
    }

    public void vaciar() { items.clear(); }

    public Long getUsuarioId() { return usuarioId; }
    public List<ItemCarrito> getItems() { return items; }
}
