# Arquitectura general
El navegador consulta al API Gateway (8080), que enruta por prefijos a cuatro microservicios HTTP independientes. Cada servicio de dominio posee su PostgreSQL exclusivo; órdenes, catálogo y notificaciones utilizan RabbitMQ para eventos.

**Flujo futuro:** una orden creada genera `orden.creada`; catálogo consume ese evento y publica `stock.descontado` o `stock.insuficiente`; órdenes y notificaciones reciben estos eventos mediante colas independientes. Esta es una propuesta preparada, no un flujo de negocio completo implementado.
