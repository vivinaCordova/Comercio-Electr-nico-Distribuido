CREATE TABLE notificaciones (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    titulo VARCHAR(120) NOT NULL,
    mensaje VARCHAR(500) NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notificaciones_usuario_fecha
    ON notificaciones (user_id, fecha_creacion DESC);
