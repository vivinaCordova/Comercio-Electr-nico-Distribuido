package com.ecommerce.ordenes.config;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration public class RabbitJsonConfig { @Bean Jackson2JsonMessageConverter jsonConverter() {return new Jackson2JsonMessageConverter();} }
