# Arquitectura general

El sistema separa frontend, Gateway y servicios de dominio. El navegador solo
habla con el Gateway en el puerto 8080; este aplica CORS para
`http://localhost:5173` y enruta las solicitudes. Cada dominio usa una base
PostgreSQL propia. La tabla de puertos y responsabilidades está en el
[README raíz](../../README.md).

Catálogo administra productos, categorías, stock e imágenes. La ficha de
producto puede incluir peso y especificaciones. Órdenes consulta y valida
productos por REST antes de publicar la orden. Carrito mantiene los ítems por
usuario y Usuarios gestiona registro y autenticación BCrypt.

El flujo asíncrono usa el exchange topic `ecommerce.events`: Órdenes publica
`orden.creada`; Catálogo descuenta stock todo-o-nada y publica
`stock.descontado` o `stock.insuficiente`; Órdenes actualiza el estado y
Notificaciones recibe el evento en su propia cola. Notificaciones aún está
pendiente de integración funcional.

Consulte [componentes](../diagramas/componentes.mmd) y el
[flujo de compra](../diagramas/flujo-compra.mmd) para los diagramas.
