# E-commerce Distribuido
## Sistemas Distribuidos, UNL

Tienda en línea construida con microservicios independientes.  
Los servicios se comunican por REST y mediante eventos con RabbitMQ.  
El frontend consume únicamente el API Gateway.

## Arquitectura

| Servicio | Puerto | Base de datos | Responsabilidad |
|---|---:|---|---|
| api-gateway | 8080 | — | Enrutamiento y único emisor de CORS |
| ms-catalogo | 8081 | `catalogo_db` | Productos, categorías, stock, imágenes y validación |
| ms-ordenes | 8082 | `ordenes_db` | Órdenes, promoción SELL12 e historial |
| ms-carrito | 8083 | `carrito_db` | Carrito por usuario; se ejecuta con Docker |
| ms-notificaciones | 8084 | `notificaciones_db` | Pendiente de integrar: correo por MailHog y WebSocket |
| ms-usuarios | 8085 | `usuarios_db` | Registro y login con BCrypt; correos `@unl.edu.ec` |
| frontend | 5173 | — | React 18 + Vite |

Cada servicio mantiene su propia base PostgreSQL. La configuración detallada se
encuentra en [docs/arquitectura](./docs/arquitectura/arquitectura-general.md).

## Flujo de compra

1. El usuario inicia sesión.
2. Explora el catálogo por categorías.
3. Consulta el detalle del producto.
4. Agrega productos al carrito y aplica `SELL12` (12%); el descuento también se calcula en el servidor.
5. Revisa la factura.
6. El frontend envía `POST /ordenes`.
7. Órdenes valida cada producto contra Catálogo por REST:
   `POST /api/catalogo/productos/{id}/validar`.
8. Órdenes publica `orden.creada` en el exchange `ecommerce.events`.
9. Catálogo descuenta stock de forma todo-o-nada y publica `stock.descontado` o `stock.insuficiente`.
10. Órdenes cambia la orden a `CONFIRMADA` o `CANCELADA`, con el motivo cuando corresponde.
11. El usuario consulta “Mis órdenes”.

## Mensajería

RabbitMQ usa el exchange topic `ecommerce.events` y estas colas:

- `catalogo.orden-creada`
- `ordenes.stock-eventos`
- `notificaciones.stock-eventos`

Los bindings y routing keys están en
[infrastructure/rabbitmq/definitions.json](./infrastructure/rabbitmq/definitions.json).

## Requisitos

- Java 21
- Maven 3.9+
- Node.js 20.19+ o 22+ y npm
- PostgreSQL 16
- RabbitMQ 3.13 (puede ejecutarse con Docker)
- `jq` y `curl` para el seed

## Puesta en marcha en Linux/macOS

Desde `ecommerce-distribuido/`:

1. Inicie PostgreSQL y RabbitMQ. Ejemplo para RabbitMQ:

   ```bash
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 \
     -e RABBITMQ_DEFAULT_USER="$RABBITMQ_USER" \
     -e RABBITMQ_DEFAULT_PASS="$RABBITMQ_PASSWORD" rabbitmq:3.13-management
   ```

   Cree el rol `ecommerce` y ejecute
   `infrastructure/postgres/crear_bases.sql` con `psql`.
2. Copie `.env.example` a `.env` y defina `POSTGRES_PASSWORD` y
   `RABBITMQ_PASSWORD`. El archivo `.env` no se sube.
3. Arranque en este orden:
   `ms-catalogo`, `ms-ordenes`, `ms-usuarios`, `ms-carrito` (consulte
   [backend/ms-carrito/docker-compose.yml](./backend/ms-carrito/docker-compose.yml)),
   `api-gateway` y luego el frontend:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```
4. Cargue datos de demostración:

   ```bash
   bash infrastructure/seed/cargar-productos.sh
   ```

   Use `--reimagenes` para reemplazar imágenes. Las fotos locales deben estar
   en `infrastructure/seed/imagenes/` con nombre `<SKU>.jpg`, `.png` o `.webp`.
5. Abra siempre http://localhost:5173. El Gateway solo permite ese origen por CORS.

## Puesta en marcha en Windows

1. Inicie PostgreSQL y RabbitMQ con Docker Desktop o instalaciones locales.
   Cree el rol `ecommerce` y ejecute
   `infrastructure/postgres/crear_bases.sql` desde `psql`.
2. Copie `.env.example` a `.env` y defina `POSTGRES_PASSWORD` y
   `RABBITMQ_PASSWORD` en la terminal o en el archivo local.
3. Arranque los servicios con Maven desde cada carpeta:

   ```powershell
   mvn spring-boot:run
   ```

   Use el orden `ms-catalogo`, `ms-ordenes`, `ms-usuarios`, `ms-carrito`,
   `api-gateway` y frontend.
4. En `frontend/`, ejecute `npm install` y `npm run dev`. Para el seed, use
   Git Bash o WSL:

   ```bash
   bash infrastructure/seed/cargar-productos.sh --reimagenes
   ```

5. Abra siempre http://localhost:5173.

## Cuenta y correo

La cuenta de demostración es `admin@unl.edu.ec`. La contraseña la define
`ms-usuarios`; no se repite aquí porque no está documentada en su README.
Todos los correos deben terminar en `@unl.edu.ec`.

## Pruebas

Desde cada servicio Java:

```bash
mvn test
```

Para el frontend:

```bash
cd frontend
npm run build
```

## Estado y pendientes

- Falta un Docker Compose único para todo el sistema.
- `ms-notificaciones` está pendiente de integrar.
- Los pagos son simulados mediante `simularPago`.
- No hay JWT.
- Las pruebas de integración están pendientes.

## Uso de IA

Consulte la Declaratoria de IA del grupo para conocer el uso de herramientas de
inteligencia artificial en este proyecto.
