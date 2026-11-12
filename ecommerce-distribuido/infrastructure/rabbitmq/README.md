# RabbitMQ local

Instale RabbitMQ en su equipo y habilite su interfaz de administracion.
Cree usuario y permisos locales segun .env.
Importe `definitions.json` desde RabbitMQ Management > Overview > Import definitions, si corresponde a su instalacion.
La topologia contiene exchange `ecommerce.events` y las tres colas con sus bindings.
No hay procesos Docker en este proyecto.
