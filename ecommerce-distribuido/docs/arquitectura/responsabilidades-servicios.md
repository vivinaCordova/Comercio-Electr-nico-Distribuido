# Responsabilidades de los servicios

- **api-gateway:** punto de entrada, enrutamiento y CORS.
- **ms-catalogo:** productos activos, categorías, precios, stock, imágenes y
  validación para órdenes. La ficha admite descripción, peso y especificaciones.
- **ms-ordenes:** creación e historial de órdenes, promoción SELL12, pago
  simulado y coordinación de eventos de stock.
- **ms-carrito:** carrito e ítems por usuario; corre en Docker.
- **ms-usuarios:** registro y login, validación de correos `@unl.edu.ec` y
  contraseñas BCrypt.
- **ms-notificaciones:** destino previsto para avisos por correo y WebSocket;
  pendiente de integración.
- **frontend:** navegación, autenticación, catálogo, detalle, carrito, factura
  y consulta de órdenes.

La separación de bases y las rutas públicas se describen en el
[README raíz](../../README.md) y en la
[referencia de endpoints](../api/endpoints.md).
