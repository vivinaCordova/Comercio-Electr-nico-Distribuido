# Verificación
- La estructura y la integridad ZIP se comprueban al generar el paquete.
- Los POM y la configuración RabbitMQ se validan como XML/JSON.
- Sin ejecucion local, Maven ni conexión de red a repositorios no es posible ejecutar `docker compose`, compilar los servicios ni instalar dependencias del frontend en este entorno.
- Verificación recomendada en equipo con ejecucion local: `docker compose config`, `docker compose build`, `docker compose up -d`, `docker compose ps`, `curl http://localhost:8080/actuator/health`.
