package com.ecommerce.usuarios.service;

import com.ecommerce.usuarios.config.ErrorApi;
import com.ecommerce.usuarios.dto.Dtos.*;
import com.ecommerce.usuarios.entity.Usuario;
import com.ecommerce.usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository repo) { this.repo = repo; }

    public UsuarioDto registrar(RegistroRequest r) {
        String email = r.email().trim().toLowerCase();
        if (repo.existsByEmail(email)) throw new ErrorApi(HttpStatus.CONFLICT, "Ese correo ya está registrado.");
        return UsuarioDto.de(repo.save(Usuario.nuevo(r.nombre().trim(), email, encoder.encode(r.password()), "CLIENTE")));
    }

    public UsuarioDto login(LoginRequest r) {
        Usuario u = repo.findByEmail(r.email().trim().toLowerCase())
            .filter(x -> encoder.matches(r.password(), x.getPasswordHash()))
            .orElseThrow(() -> new ErrorApi(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));
        return UsuarioDto.de(u);
    }

    public void crearAdminSiNoExiste(String email, String password) {
        if (!repo.existsByEmail(email)) repo.save(Usuario.nuevo("Administrador", email, encoder.encode(password), "ADMIN"));
    }
}
