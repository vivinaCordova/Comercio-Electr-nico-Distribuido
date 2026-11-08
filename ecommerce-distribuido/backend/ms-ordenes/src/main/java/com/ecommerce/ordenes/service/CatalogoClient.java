package com.ecommerce.ordenes.service;

import com.ecommerce.ordenes.dto.CatalogoValidationRequest;
import com.ecommerce.ordenes.dto.CatalogoValidationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CatalogoClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CatalogoClient(RestTemplate restTemplate,
                          @Value("${catalogo.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public CatalogoValidationResponse validar(Long productoId, CatalogoValidationRequest request) {
        try {
            ResponseEntity<CatalogoValidationResponse> response = restTemplate.postForEntity(
                    baseUrl + "/api/catalogo/productos/" + productoId + "/validar",
                    request, CatalogoValidationResponse.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new OrdenValidationException("El catálogo devolvió una respuesta inválida");
            }
            return response.getBody();
        } catch (RestClientException exception) {
            throw new OrdenValidationException("No fue posible validar el producto en ms-catalogo", exception);
        }
    }
}
