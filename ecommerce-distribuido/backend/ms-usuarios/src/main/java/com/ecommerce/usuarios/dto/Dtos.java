package com.ecommerce.usuarios.dto;

import com.ecommerce.usuarios.entity.Usuario;
import jakarta.validation.constraints.*;

public final class Dtos {
    private Dtos() { }

    public record RegistroRequest(
        @NotBlank @Size(min = 2, max = 50) @Pattern(regexp = "^[\\p{L}][\\p{L}\\s'.-]*$", message = "Nombre inválido") String nombre,
        @NotBlank @Size(max = 254) @Pattern(regexp = "^[a-z0-9]+([._%+-][a-z0-9]+)*@unl\\.edu\\.ec$", message = "Usa tu correo institucional @unl.edu.ec") String email,
        @NotBlank @Size(min = 8, max = 64) String password) { }

    public record LoginRequest(@NotBlank @Size(max = 254) String email, @NotBlank @Size(max = 64) String password) { }

    public record UsuarioDto(Long id, String nombre, String email, String rol) {
        public static UsuarioDto de(Usuario u) { return new UsuarioDto(u.getId(), u.getNombre(), u.getEmail(), u.getRol()); }
    }
}
