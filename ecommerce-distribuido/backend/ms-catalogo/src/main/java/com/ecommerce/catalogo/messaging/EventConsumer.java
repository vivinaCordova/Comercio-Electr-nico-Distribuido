
package com.ecommerce.catalogo.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    name = "app.rabbitmq.enabled",
    havingValue = "true"
)
public class EventConsumer {

    @RabbitListener(queues = "catalogo.orden-creada")
    public void recibir(EventMessage evento) {
        // TODO: implementar reglas empresariales en una fase futura.
    }
}
