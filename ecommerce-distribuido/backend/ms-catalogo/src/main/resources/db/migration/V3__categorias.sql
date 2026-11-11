CREATE TABLE categoria (
    id          BIGSERIAL     PRIMARY KEY,
    nombre      VARCHAR(80)   NOT NULL,
    slug        VARCHAR(80)   NOT NULL,
    icono       VARCHAR(16)   NOT NULL,
    descripcion VARCHAR(200)  NOT NULL,
    CONSTRAINT uk_categoria_slug   UNIQUE (slug),
    CONSTRAINT uk_categoria_nombre UNIQUE (nombre)
);

INSERT INTO categoria (nombre, slug, icono, descripcion) VALUES
    ('Electrónica',          'electronica',         '💻', 'Laptops, teléfonos, audífonos, tablets'),
    ('Ropa y accesorios',    'ropa-y-accesorios',   '👕', 'Camisetas, pantalones, zapatos, relojes'),
    ('Hogar',                'hogar',               '🏠', 'Muebles, decoración, utensilios de cocina'),
    ('Alimentos',            'alimentos',           '🥑', 'Snacks, bebidas, productos orgánicos'),
    ('Belleza y salud',      'belleza-y-salud',     '💄', 'Perfumes, maquillaje, vitaminas'),
    ('Deportes',             'deportes',            '⚽', 'Balones, bicicletas, ropa deportiva'),
    ('Libros y educación',   'libros-y-educacion',  '📚', 'Libros físicos, e-books, cursos en línea'),
    ('Videojuegos',          'videojuegos',         '🎮', 'Consolas, juegos, accesorios gamer'),
    ('Mascotas',             'mascotas',            '🐾', 'Alimentos, juguetes, camas'),
    ('Automotriz',           'automotriz',          '🚗', 'Repuestos, llantas, accesorios para vehículos');

ALTER TABLE productos ADD COLUMN categoria_id BIGINT;

ALTER TABLE productos
    ADD CONSTRAINT fk_productos_categoria
    FOREIGN KEY (categoria_id) REFERENCES categoria(id);

CREATE INDEX idx_productos_categoria ON productos (categoria_id);

-- Los productos que ya existían son de Electrónica
UPDATE productos
SET categoria_id = (SELECT id FROM categoria WHERE slug = 'electronica');
