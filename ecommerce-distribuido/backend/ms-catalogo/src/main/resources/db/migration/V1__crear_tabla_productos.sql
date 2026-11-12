CREATE TABLE productos (
    id                  BIGSERIAL      PRIMARY KEY,
    nombre              VARCHAR(150)   NOT NULL,
    descripcion         VARCHAR(1000),
    precio              NUMERIC(12, 2) NOT NULL,
    stock               INTEGER        NOT NULL,
    sku                 VARCHAR(50)    NOT NULL,
    activo              BOOLEAN        NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_productos_sku     UNIQUE (sku),
    CONSTRAINT ck_productos_precio  CHECK (precio > 0),
    CONSTRAINT ck_productos_stock   CHECK (stock >= 0)
);

CREATE INDEX idx_productos_nombre_lower ON productos (LOWER(nombre));
CREATE INDEX idx_productos_activo       ON productos (activo);
