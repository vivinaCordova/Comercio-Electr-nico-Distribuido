package com.ecommerce.ordenes.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "ecommerce.events";
    public static final String STOCK_QUEUE = "ordenes.stock-eventos";

    @Bean
    TopicExchange ecommerceEventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue stockEventsQueue() {
        return QueueBuilder.durable(STOCK_QUEUE).build();
    }

    @Bean
    Binding stockEventsBinding(Queue stockEventsQueue, TopicExchange ecommerceEventsExchange) {
        return BindingBuilder.bind(stockEventsQueue).to(ecommerceEventsExchange).with("stock.*");
    }

    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
