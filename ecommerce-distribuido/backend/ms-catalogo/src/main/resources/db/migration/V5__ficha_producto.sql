-- Amplía el catálogo sin modificar las migraciones históricas.
ALTER TABLE productos
    ADD COLUMN peso_gramos INTEGER,
    ADD COLUMN especificaciones TEXT;

ALTER TABLE productos
    ADD CONSTRAINT ck_productos_peso_gramos CHECK (peso_gramos IS NULL OR peso_gramos > 0);
