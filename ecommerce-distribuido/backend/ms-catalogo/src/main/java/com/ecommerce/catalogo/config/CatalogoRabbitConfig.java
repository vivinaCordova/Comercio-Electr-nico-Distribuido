package com.ecommerce.catalogo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogoRabbitConfig {

    @Bean
    Queue catalogoOrdenCreadaQueue() {
        return new Queue("catalogo.orden-creada", true);
    }

    @Bean
    TopicExchange ecommerceEventsExchange() {
        return new TopicExchange("ecommerce.events", true, false);
    }

    @Bean
    Binding catalogoOrdenCreadaBinding(Queue catalogoOrdenCreadaQueue,
                                       TopicExchange ecommerceEventsExchange) {
        return BindingBuilder.bind(catalogoOrdenCreadaQueue)
                .to(ecommerceEventsExchange)
                .with("orden.creada");
    }
}
