package com.ecommerce.notificaciones.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class RabbitConfig {
    @Bean TopicExchange ecommerceEvents() { return new TopicExchange("ecommerce.events", true, false); }
    @Bean Queue notificacionesStock() { return new Queue("notificaciones.stock-eventos", true); }
    @Bean Queue notificacionesOrden() { return new Queue("notificaciones.orden-creada", true); }
    @Bean Binding stockConfirmado(@Qualifier("ecommerceEvents") TopicExchange exchange,
                                  @Qualifier("notificacionesStock") Queue notificacionesStock) {
        return BindingBuilder.bind(notificacionesStock).to(exchange).with("stock.descontado");
    }
    @Bean Binding stockInsuficiente(@Qualifier("ecommerceEvents") TopicExchange exchange,
                                    @Qualifier("notificacionesStock") Queue notificacionesStock) {
        return BindingBuilder.bind(notificacionesStock).to(exchange).with("stock.insuficiente");
    }
    @Bean Binding ordenCreada(@Qualifier("ecommerceEvents") TopicExchange exchange,
                              @Qualifier("notificacionesOrden") Queue notificacionesOrden) {
        return BindingBuilder.bind(notificacionesOrden).to(exchange).with("orden.creada");
    }
}
