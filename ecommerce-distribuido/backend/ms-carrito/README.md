# ms-carrito

Servicio de carrito por usuario. Expone su API en el puerto 8083 y utiliza
`carrito_db` en PostgreSQL.

## Ejecución con Docker Compose

Desde esta carpeta, defina obligatoriamente `POSTGRES_PASSWORD` y ejecute:

```bash
export POSTGRES_PASSWORD='una-clave-local'
docker compose up --build
```

Compose usa la misma variable para PostgreSQL y para `ms-carrito`; no hay
contraseñas escritas en el archivo.

## API

- `GET /api/carrito/{usuarioId}`
- `POST /api/carrito/{usuarioId}/items`
- `PUT /api/carrito/{usuarioId}/items/{productoId}`
- `DELETE /api/carrito/{usuarioId}/items/{productoId}`
- `DELETE /api/carrito/{usuarioId}`
- `GET /api/carrito/health`
