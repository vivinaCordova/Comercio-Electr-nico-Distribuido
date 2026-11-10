# ms-ordenes

Proyecto Spring Boot independiente para ejecucion local.

Requisitos: Java 21, Maven 3.9+; los servicios de infraestructura necesarios deben estar iniciados en localhost.

Desde esta carpeta: `mvn spring-boot:run`.

Configuracion: `src/main/resources/application.yml`; variables de entorno definidas en el README raiz.

## API local

`POST /ordenes` recibe:

```json
{
  "userId": 10,
  "items": [
    { "productoId": 1, "cantidad": 2, "precioEsperado": 19.99 }
  ]
}
```

Cada producto se valida síncronamente contra
`POST ${CATALOGO_URL}/api/catalogo/productos/{id}/validar`. El catálogo debe
responder `{ "valido": true, "precio": 19.99, "detalle": "..." }`.
Después de persistir, se publica `orden.creada` en `ecommerce.events`.
Los eventos `stock.descontado` y `stock.insuficiente` actualizan la orden a
`CONFIRMADA` o `CANCELADA`, respectivamente.

La base de datos local usa `ordenes_db`; `JPA_DDL_AUTO` permite cambiar el
modo de generación del esquema (por defecto, `update` para desarrollo local).
