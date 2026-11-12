# ms-notificaciones

Servicio que consume eventos de órdenes y guarda notificaciones por usuario.
Escucha en el puerto 8084 y usa `notificaciones_db` con JPA y Flyway.

## Variables de entorno

`DB_HOST`, `DB_PORT`, `POSTGRES_USER`, `POSTGRES_PASSWORD`,
`RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER`, `RABBITMQ_PASSWORD`,
`SMTP_HOST` y `SMTP_PORT`. La configuración tiene valores locales de
desarrollo; define las contraseñas mediante el entorno y no las guardes en Git.

## Ejecución

```bash
mvn spring-boot:run
```

La base debe existir como `notificaciones_db`. Flyway crea la tabla al iniciar.

## API

- `GET /api/notificaciones?userId=1&soloNoLeidas=false`
- `GET /api/notificaciones/no-leidas/count?userId=1`
- `PATCH /api/notificaciones/{id}/leida`
- `POST /api/notificaciones/leer-todas?userId=1`
- `GET /api/notificaciones/health`

El consumidor escucha `orden.creada`, `stock.descontado` y
`stock.insuficiente` en `ecommerce.events`.
