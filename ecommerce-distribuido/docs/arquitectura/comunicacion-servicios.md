# Comunicación
REST: `/api/catalogo/**`, `/api/ordenes/**`, `/api/carrito/**`, `/api/notificaciones/**`.
Exchange `ecommerce.events` de tipo topic. Colas y bindings se crean una sola vez desde `infrastructure/rabbitmq/definitions.json`.
Bindings: catalogo.orden-creada→orden.creada; ordenes.stock-eventos y notificaciones.stock-eventos→stock.descontado y stock.insuficiente.
El WebSocket STOMP de notificaciones está en `/ws/notificaciones` (puerto interno 8084), con destinos `/topic/**`.
