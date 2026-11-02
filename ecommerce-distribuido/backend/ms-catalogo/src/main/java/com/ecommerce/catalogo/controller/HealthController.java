package com.ecommerce.catalogo.controller;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController public class HealthController { @GetMapping("/api/catalogo/health") public Map<String,String> health() { return Map.of("servicio","ms-catalogo","estado","base-preparada"); } }
