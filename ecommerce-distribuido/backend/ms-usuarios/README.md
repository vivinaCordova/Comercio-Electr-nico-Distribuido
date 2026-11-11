# ms-usuarios

Servicio de registro y autenticación de usuarios. Escucha en el puerto 8085,
usa `usuarios_db` y almacena contraseñas con BCrypt.

## Endpoints

- `POST /api/usuarios/registro`: crea un usuario y responde `201`.
- `POST /api/usuarios/login`: valida credenciales y devuelve el usuario.

El correo debe terminar en `@unl.edu.ec`. La contraseña debe tener entre 8 y
64 caracteres. Un correo existente responde `409`; credenciales inválidas,
`401`; datos inválidos, `400` o `422` según la validación aplicada.

## Variables y ejecución

Configure `POSTGRES_PASSWORD`, además de los valores de conexión definidos en
`src/main/resources/application.yml`. Desde esta carpeta:

```bash
mvn spring-boot:run
```

La cuenta administradora inicial se crea mediante el mecanismo de inicialización
configurado por el servicio (seed o datos de arranque). Si no existe, registre
un usuario institucional desde `/api/usuarios/registro`; no se documenta una
contraseña fija en el repositorio.
