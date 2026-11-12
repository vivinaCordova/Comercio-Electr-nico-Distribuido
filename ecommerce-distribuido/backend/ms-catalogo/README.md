# ms-catalogo

Proyecto Spring Boot independiente para ejecucion local.

Requisitos: Java 21, Maven 3.9+; los servicios de infraestructura necesarios deben estar iniciados en localhost.

Desde esta carpeta: `mvn spring-boot:run`.

Configuracion: `src/main/resources/application.yml`; variables de entorno definidas en el README raiz.

El detalle completo de un producto (categoría, imágenes y especificaciones) está disponible en
`GET /api/catalogo/productos/{id}/detalle`.

La funcionalidad es inicial y no constituye un sistema comercial completo.
