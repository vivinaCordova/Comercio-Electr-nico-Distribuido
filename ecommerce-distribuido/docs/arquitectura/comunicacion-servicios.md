# Comunicación entre servicios

## REST

El frontend consume el Gateway. Este enruta `/api/catalogo/**`,
`/api/carrito/**`, `/api/usuarios/**`, `/api/notificaciones/**` y las rutas de
órdenes. Órdenes llama a Catálogo para validar cada producto antes de crear
una orden. La validación incluye disponibilidad y precio; no se comparte una
base de datos entre servicios.

## Eventos

RabbitMQ publica en el exchange topic `ecommerce.events`. Las colas se
declaran en `infrastructure/rabbitmq/definitions.json`:

- `catalogo.orden-creada` consume `orden.creada`.
- `ordenes.stock-eventos` consume `stock.descontado` y `stock.insuficiente`.
- `notificaciones.stock-eventos` consume ambos eventos de stock.

La secuencia completa está en [flujo-compra](../diagramas/flujo-compra.mmd).
El WebSocket de Notificaciones está previsto en `/ws/notificaciones`, pero la
integración del servicio sigue pendiente.
