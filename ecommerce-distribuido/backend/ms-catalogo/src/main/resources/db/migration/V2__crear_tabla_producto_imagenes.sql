
CREATE TABLE producto_imagenes (
    id BIGSERIAL PRIMARY KEY,

    producto_id BIGINT NOT NULL,

    ruta_imagen VARCHAR(500) NOT NULL,

    es_principal BOOLEAN NOT NULL DEFAULT FALSE,

    orden INTEGER NOT NULL DEFAULT 0,

    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_imagen_producto
        FOREIGN KEY (producto_id)
        REFERENCES productos(id)
        ON DELETE CASCADE,

    CONSTRAINT ck_imagen_orden
        CHECK (orden >= 0)
);

CREATE INDEX idx_imagen_producto
    ON producto_imagenes(producto_id);

CREATE UNIQUE INDEX idx_imagen_principal_unica
    ON producto_imagenes(producto_id)
    WHERE es_principal = TRUE;

