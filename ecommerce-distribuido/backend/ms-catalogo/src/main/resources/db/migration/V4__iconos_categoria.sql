-- El campo icono guarda una clave que el frontend traduce a un SVG (sin emojis)
UPDATE categoria SET icono = CASE slug
    WHEN 'electronica'        THEN 'laptop'
    WHEN 'ropa-y-accesorios'  THEN 'camiseta'
    WHEN 'hogar'              THEN 'hogar'
    WHEN 'alimentos'          THEN 'alimentos'
    WHEN 'belleza-y-salud'    THEN 'belleza'
    WHEN 'deportes'           THEN 'deportes'
    WHEN 'libros-y-educacion' THEN 'libros'
    WHEN 'videojuegos'        THEN 'videojuegos'
    WHEN 'mascotas'           THEN 'mascotas'
    WHEN 'automotriz'         THEN 'automotriz'
END;
