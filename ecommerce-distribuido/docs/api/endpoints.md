# Endpoints HTTP

Las rutas de esta página son las rutas públicas del Gateway en
`http://localhost:8080`. El Gateway enruta `/ordenes/**` directamente a
ms-ordenes y conserva los prefijos `/api/**` de los demás servicios.

## Salud

| Servicio | Método | Ruta | Descripción | Cuerpo/parámetros |
|---|---|---|---|---|
| Gateway | GET | `/actuator/health` | Salud del Gateway | — |
| Catálogo | GET | `/api/catalogo/health` | Salud del catálogo | — |
| Órdenes | GET | `/api/ordenes/health` | Salud de órdenes | — |
| Carrito | GET | `/api/carrito/health` | Salud del carrito | — |
| Notificaciones | GET | `/api/notificaciones/health` | Salud de notificaciones | — |
| Usuarios | GET | `/actuator/health` | Salud del servicio de usuarios | — |

## Catálogo

| Método | Ruta | Descripción | Cuerpo/parámetros |
|---|---|---|---|
| POST | `/api/catalogo/productos` | Crea un producto | JSON con `nombre`, `descripcion`, `precio`, `stock`, `sku`, `activo` y `categoriaId` |
| GET | `/api/catalogo/productos` | Lista productos paginados | `pagina`, `tamanio`, `ordenarPor`, `direccion` |
| GET | `/api/catalogo/productos/activos` | Lista productos activos | `pagina`, `tamanio`, `ordenarPor`, `direccion` |
| GET | `/api/catalogo/productos/buscar` | Busca por nombre | `nombre`, `pagina`, `tamanio`, `ordenarPor`, `direccion` |
| GET | `/api/catalogo/productos/{id}` | Obtiene un producto | `id` de ruta |
| PUT | `/api/catalogo/productos/{id}` | Actualiza un producto | JSON de producto completo |
| DELETE | `/api/catalogo/productos/{id}` | Desactiva un producto | `id` de ruta |
| POST | `/api/catalogo/productos/{id}/validar` | Valida disponibilidad y precio para una orden | JSON de validación de stock |
| GET | `/api/catalogo/productos/{productoId}/imagenes` | Lista imágenes | `productoId` de ruta |
| POST | `/api/catalogo/productos/{productoId}/imagenes` | Sube una imagen | Multipart `archivo` |
| GET | `/api/catalogo/productos/{productoId}/imagenes/{imagenId}/archivo` | Sirve una imagen | Identificadores de ruta |
| PATCH | `/api/catalogo/productos/{productoId}/imagenes/{imagenId}/principal` | Marca imagen principal | Identificadores de ruta |
| DELETE | `/api/catalogo/productos/{productoId}/imagenes/{imagenId}` | Elimina una imagen | Identificadores de ruta |

Esta versión del controlador no expone un endpoint independiente de categorías
ni filtros `categoria` o `q`; no se documentan rutas que no existen en código.

## Órdenes

| Método | Ruta | Descripción | Cuerpo/parámetros |
|---|---|---|---|
| POST | `/ordenes` | Crea una orden | JSON `{ "userId": 1, "items": [{ "productoId": 1, "cantidad": 1 }] }` |
| GET | `/ordenes?userId={id}` | Lista el historial del usuario | `userId` obligatorio |

La creación valida productos contra Catálogo, calcula la promoción SELL12 y
publica el evento de orden para el flujo de stock.

## Carrito

| Método | Ruta | Descripción | Cuerpo/parámetros |
|---|---|---|---|
| GET | `/api/carrito/{usuarioId}` | Obtiene el carrito | `usuarioId` de ruta |
| POST | `/api/carrito/{usuarioId}/items` | Agrega un producto | JSON `{ "productoId": 1, "cantidad": 1 }` |
| PUT | `/api/carrito/{usuarioId}/items/{productoId}` | Cambia cantidad | JSON `{ "productoId": 1, "cantidad": 2 }` |
| DELETE | `/api/carrito/{usuarioId}/items/{productoId}` | Quita un producto | Identificadores de ruta |
| DELETE | `/api/carrito/{usuarioId}` | Vacía el carrito | `usuarioId` de ruta |

## Usuarios

| Método | Ruta | Descripción | Cuerpo/parámetros |
|---|---|---|---|
| POST | `/api/usuarios/registro` | Registra un usuario | JSON `{ "nombre": "...", "email": "...@unl.edu.ec", "password": "..." }`; responde 201 |
| POST | `/api/usuarios/login` | Inicia sesión | JSON `{ "email": "...", "password": "..." }` |

El correo de registro debe ser institucional `@unl.edu.ec` y la contraseña debe
tener entre 8 y 64 caracteres.

## Errores comunes

- `400`: JSON inválido, campos obligatorios ausentes o validación fallida.
- `401`: credenciales incorrectas en login.
- `404`: producto, imagen o recurso inexistente.
- `409`: SKU duplicado o correo ya registrado.
- `422`: error de dominio o validación semántica devuelto por el servicio.
- `500`: error inesperado del servicio o dependencia.
