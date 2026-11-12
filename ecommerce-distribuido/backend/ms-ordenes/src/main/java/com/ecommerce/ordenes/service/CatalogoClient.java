package com.ecommerce.ordenes.service;

import com.ecommerce.ordenes.dto.CatalogoValidationRequest;
import com.ecommerce.ordenes.dto.CatalogoValidationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CatalogoClient {
    private static final Logger log = LoggerFactory.getLogger(CatalogoClient.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CatalogoClient(RestTemplate restTemplate,
                          @Value("${catalogo.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public CatalogoValidationResponse validar(Long productoId, CatalogoValidationRequest request) {
        log.debug("Validando productoId={} contra catalogo en {}", productoId, baseUrl);
        try {
            ResponseEntity<CatalogoValidationResponse> response = restTemplate.postForEntity(
                    baseUrl + "/api/catalogo/productos/" + productoId + "/validar",
                    request, CatalogoValidationResponse.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new OrdenValidationException("El catálogo devolvió una respuesta inválida");
            }
            log.debug("Catalogo valido para productoId={}: valido={}, precio={}",
                    productoId, response.getBody().valido(), response.getBody().precio());
            return response.getBody();
        } catch (RestClientException exception) {
            log.error("Error comunicando con catalogo para productoId={}", productoId, exception);
            throw new OrdenValidationException("No fue posible validar el producto en ms-catalogo", exception);
        }
    }
}
