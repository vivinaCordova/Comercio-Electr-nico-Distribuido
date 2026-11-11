package com.ecommerce.notificaciones.controller;

import com.ecommerce.notificaciones.dto.NotificacionDto;
import com.ecommerce.notificaciones.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    private final NotificacionService service;
    public NotificacionController(NotificacionService service) { this.service = service; }

    @GetMapping
    public List<NotificacionDto> listar(@RequestParam Long userId,
                                        @RequestParam(defaultValue = "false") boolean soloNoLeidas) {
        return service.listar(userId, soloNoLeidas).stream().map(NotificacionDto::from).toList();
    }

    @GetMapping("/no-leidas/count")
    public Map<String, Long> contar(@RequestParam Long userId) {
        return Map.of("count", service.contarNoLeidas(userId));
    }

    @PatchMapping("/{id}/leida")
    public ResponseEntity<Void> marcar(@PathVariable Long id) {
        service.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodas(@RequestParam Long userId) {
        service.marcarTodas(userId);
        return ResponseEntity.noContent().build();
    }
}
