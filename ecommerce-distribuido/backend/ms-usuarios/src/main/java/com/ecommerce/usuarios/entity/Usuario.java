package com.ecommerce.usuarios.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    private String rol;

    protected Usuario() { }

    // Factory Method: única forma de crear un usuario con rol coherente
    public static Usuario nuevo(String nombre, String email, String passwordHash, String rol) {
        Usuario u = new Usuario();
        u.nombre = nombre; u.email = email; u.passwordHash = passwordHash; u.rol = rol;
        return u;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRol() { return rol; }
}
