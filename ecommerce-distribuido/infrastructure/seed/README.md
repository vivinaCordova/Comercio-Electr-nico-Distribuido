# Imágenes locales del catálogo

Coloca las imágenes de demostración en `infrastructure/seed/imagenes/` con el
SKU exacto como nombre: `IPH-17PRO.jpg`, `SAM-S25ULTRA.png` o
`ELE-LAP-VIVO15.webp`. El seed valida el tipo MIME real (`JPEG`, `PNG` o
`WEBP`) antes de subirlas. La carpeta está ignorada por Git para no subir
fotografías ni archivos pesados al repositorio.

Si no existe una imagen local válida, el seed genera una imagen clara de
respaldo con el icono textual de la categoría y el nombre del producto.
