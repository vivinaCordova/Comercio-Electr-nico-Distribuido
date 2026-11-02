# E-commerce distribuido — proyecto base local

Arquitectura academica de microservicios independientes, SIN Docker ni Docker Compose. Es una base inicial; no es una tienda terminada.

## Tecnologias
- Java 21, Spring Boot 3.3.7, Spring Cloud 2023.0.5, Maven 3.9+
- React 18, Vite 6, Node.js 20.19+ o 22+ y npm
- PostgreSQL 16+, RabbitMQ 3.13+, MailHog opcional para SMTP local

## Estructura
- `frontend/`: SPA React, paginas y clientes HTTP.
- `backend/api-gateway/`: puerta de entrada REST en puerto 8080.
- `backend/ms-catalogo/`: catalogo en puerto 8081.
- `backend/ms-ordenes/`: ordenes en puerto 8082.
- `backend/ms-carrito/`: carrito en puerto 8083.
- `backend/ms-notificaciones/`: notificaciones y WebSocket en puerto 8084.
- `infrastructure/`: referencias de configuracion local de PostgreSQL, RabbitMQ y SMTP.
- `scripts/`: scripts para Windows y verificacion estatica.
- `docs/`: documentacion de arquitectura y API.

## Preparacion en Windows
1. Instale Java 21 (JAVA_HOME), Maven 3.9+ (PATH), Node.js y npm.
2. Instale PostgreSQL localmente; cree el rol `ecommerce` con su propia contrasena y ejecute `infrastructure/postgres/crear_bases.sql` con psql.
3. Instale RabbitMQ localmente, cree el usuario ecommerce, configure acceso al vhost `/` e importe `infrastructure/rabbitmq/definitions.json` si utiliza esa topologia.
4. Opcional: instale MailHog para SMTP 1025.
5. Desde la raiz: `Copy-Item .env.example .env` y cambie las contrasenas. Este archivo no se debe subir a Git.
6. Abra una terminal independiente para cada microservicio. Desde la raiz:
   - `powershell -ExecutionPolicy Bypass -File scripts/iniciar-backend.ps1 ms-catalogo`
   - `powershell -ExecutionPolicy Bypass -File scripts/iniciar-backend.ps1 ms-ordenes`
   - `powershell -ExecutionPolicy Bypass -File scripts/iniciar-backend.ps1 ms-carrito`
   - `powershell -ExecutionPolicy Bypass -File scripts/iniciar-backend.ps1 ms-notificaciones`
   - `powershell -ExecutionPolicy Bypass -File scripts/iniciar-backend.ps1 api-gateway`
7. Para el frontend, en otra terminal: `powershell -ExecutionPolicy Bypass -File scripts/iniciar-frontend.ps1`.

Alternativa: entre a cada proyecto Java y ejecute `mvn spring-boot:run`; recuerde configurar las variables de entorno en la terminal (el archivo .env no lo lee automaticamente Spring).

## Acceso local
- Frontend: http://localhost:5173
- API Gateway: http://localhost:8080/actuator/health
- Catalogo: http://localhost:8080/api/catalogo/health
- Ordenes: http://localhost:8080/api/ordenes/health
- Carrito: http://localhost:8080/api/carrito/health
- Notificaciones: http://localhost:8080/api/notificaciones/health
- RabbitMQ Management: http://localhost:15672 (si esta habilitado)
- MailHog Web: http://localhost:8025 (si lo instala)

## Notas
No incluye Dockerfiles ni archivos Compose. Las bases y servicios externos deben instalarse localmente.
Las entidades son esqueletos y `ddl-auto: none` no crea tablas automaticamente.
Los archivos `mvnw` son lanzadores que requieren Maven instalado, no un Maven Wrapper completo.
Pendiente: migraciones SQL, logica de compra real, pruebas de integracion, inventario, pagos y autenticacion.
