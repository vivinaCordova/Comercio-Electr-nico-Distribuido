package com.ecommerce.usuarios;

import com.ecommerce.usuarios.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UsuariosApplication {
    public static void main(String[] args) { SpringApplication.run(UsuariosApplication.class, args); }

    // Crea el administrador inicial si no existe
    @Bean
    CommandLineRunner admin(UsuarioService servicio) {
        return args -> servicio.crearAdminSiNoExiste("admin@unl.edu.ec", "admin");
    }
}
