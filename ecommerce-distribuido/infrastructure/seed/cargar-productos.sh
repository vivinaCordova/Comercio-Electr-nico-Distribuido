#!/usr/bin/env bash
# Carga el catálogo de demostración (categorías y productos con imagen) a través del API Gateway.
#
# - Idempotente: los SKU que ya existen se actualizan, sin duplicar productos.
# - No usa credenciales ni tokens: los endpoints de catálogo son públicos vía Gateway.
# - Las imágenes se leen de imagenes/<SKU> y se validan; si no existen, se genera un respaldo limpio.
#
# Uso: ./cargar-productos.sh [url_gateway] [--reimagenes]
set -euo pipefail

REIMAGENES=false
GATEWAY="${GATEWAY_URL:-http://localhost:8080}"
for argumento in "$@"; do
  if [ "$argumento" = "--reimagenes" ]; then
    REIMAGENES=true
  else
    GATEWAY="$argumento"
  fi
done
API="$GATEWAY/api/catalogo"
STOCK_INICIAL=20
FUENTE="/usr/share/fonts/liberation/LiberationSans-Bold.ttf"
TMP="$(dirname "$0")/.cargar-productos-tmp-$$"
mkdir -p "$TMP"
trap 'rm -rf "$TMP"' EXIT

for herramienta in curl jq file; do
  command -v "$herramienta" >/dev/null || { echo "Falta la herramienta: $herramienta" >&2; exit 1; }
done
IM="$(command -v magick || command -v convert || true)"
[ -n "$IM" ] || echo "Aviso: no hay ImageMagick; las imágenes que no se descarguen quedarán sin foto." >&2

# Catálogo: CATEGORIA_SLUG|SKU|NOMBRE|DESCRIPCION|PRECIO|ICONO|PESO|ESPECIFICACIONES
# Las fichas se actualizan tanto al crear como al encontrar el SKU existente.
PRODUCTOS=$(cat <<'DATOS'
electronica|IPH-17PRO|iPhone 17 Pro|Teléfono de alto rendimiento con pantalla brillante y cámara avanzada. Ideal para fotografía, trabajo y entretenimiento.|1199.00|telefono|187|Pantalla: 6.3 pulgadas;Almacenamiento: 256 GB;Batería: hasta 27 horas;Cámara: triple 48 MP
electronica|VIVO-X300|vivo X300 5G|Teléfono con conectividad 5G y cámara de alta resolución. Su diseño está pensado para productividad y entretenimiento.|780.00|telefono|190|Pantalla: 6.78 pulgadas;Almacenamiento: 256 GB;Batería: 5000 mAh;Cámara: 50 MP
electronica|SAM-S25U|Samsung Galaxy S25 Ultra|Teléfono premium con pantalla amplia y herramientas inteligentes. Su cámara versátil facilita fotos nítidas en cualquier momento.|1250.00|telefono|218|Pantalla: 6.9 pulgadas;Almacenamiento: 512 GB;Batería: 5000 mAh;Cámara: 200 MP
electronica|IPH-17|iPhone 17|Teléfono compacto con pantalla brillante y rendimiento fluido. Es adecuado para comunicación, fotografía y aplicaciones diarias.|780.00|telefono|177|Pantalla: 6.3 pulgadas;Almacenamiento: 256 GB;Batería: hasta 24 horas;Cámara: doble 48 MP
electronica|IPH-AIR|iPhone Air|Teléfono delgado y ligero con pantalla de alta resolución. Su batería y cámara acompañan el uso cotidiano.|890.00|telefono|165|Pantalla: 6.5 pulgadas;Almacenamiento: 256 GB;Batería: hasta 26 horas;Cámara: 48 MP
electronica|ONEP-13|OnePlus 13|Teléfono rápido con pantalla fluida y carga eficiente. Ofrece una experiencia equilibrada para juegos y trabajo.|750.00|telefono|210|Pantalla: 6.82 pulgadas;Almacenamiento: 256 GB;Batería: 6000 mAh;Cámara: triple 50 MP
electronica|PIX-9|Google Pixel 9|Teléfono con fotografía computacional y Android actualizado. Su formato compacto facilita el uso cotidiano.|690.00|telefono|198|Pantalla: 6.3 pulgadas;Almacenamiento: 128 GB;Batería: 4700 mAh;Cámara: doble 50 MP
electronica|RED-N14|Redmi Note 14|Teléfono accesible con pantalla amplia y batería duradera. Está diseñado para navegación, redes y multimedia.|420.00|telefono|196|Pantalla: 6.67 pulgadas;Almacenamiento: 256 GB;Batería: 5500 mAh;Cámara: 108 MP
electronica|SAM-S25ULTRA|Samsung Galaxy S25 Ultra|Teléfono premium con pantalla amplia y herramientas inteligentes. Su cámara versátil facilita fotos nítidas en cualquier momento.|1299.00|telefono|218|Pantalla: 6.9 pulgadas;Almacenamiento: 512 GB;Batería: 5000 mAh;Cámara: 200 MP
electronica|XIA-14T|Xiaomi 14T|Teléfono equilibrado para fotografía, productividad y uso diario. Ofrece buen rendimiento con carga rápida.|549.00|telefono|193|Pantalla: 6.67 pulgadas;Almacenamiento: 256 GB;Batería: 5000 mAh;Cámara: triple 50 MP
electronica|MOT-EDGE50|Motorola Edge 50|Teléfono delgado con pantalla fluida y cámara estabilizada. Su batería está pensada para una jornada completa.|399.00|telefono|180|Pantalla: 6.7 pulgadas;Almacenamiento: 256 GB;Batería: 5000 mAh;Cámara: 50 MP
electronica|HON-MAGIC6|Honor Magic 6|Teléfono moderno con pantalla de alta resolución y autonomía prolongada. Es una opción cómoda para trabajo y multimedia.|699.00|telefono|199|Pantalla: 6.8 pulgadas;Almacenamiento: 512 GB;Batería: 5600 mAh;Cámara: 50 MP
electronica|OPP-RENO12|OPPO Reno 12|Teléfono liviano con cámara de retrato y carga rápida. Su diseño combina comodidad y buen desempeño.|449.00|telefono|179|Pantalla: 6.7 pulgadas;Almacenamiento: 256 GB;Batería: 5000 mAh;Cámara: 50 MP
electronica|ZTE-AXON|ZTE Axon|Teléfono con audio envolvente y pantalla de gran tamaño. Está preparado para navegación, fotos y entretenimiento.|499.00|telefono|197|Pantalla: 6.8 pulgadas;Almacenamiento: 256 GB;Batería: 5000 mAh;Cámara: 64 MP
electronica|GOO-PIXEL9|Google Pixel 9|Teléfono con fotografía computacional y Android actualizado. Su formato compacto facilita el uso cotidiano.|799.00|telefono|198|Pantalla: 6.3 pulgadas;Almacenamiento: 128 GB;Batería: 4700 mAh;Cámara: doble 50 MP
electronica|ELE-LAP-VIVO15|Laptop ASUS Vivobook 15|Portátil con procesador Ryzen 7, 16 GB de RAM y SSD de 512 GB.|699.00|laptop|1000|Producto de calidad con materiales seleccionados y garantía de origen.
electronica|ELE-LAP-MBA13|MacBook Air 13 M3|Ultraligera con chip M3 y batería de hasta 18 horas.|1099.00|macbook|1000|Producto de calidad con materiales seleccionados y garantía de origen.
electronica|ELE-AUD-WH1000|Audífonos Sony WH-1000XM5|Cancelación de ruido líder en su clase y 30 horas de batería.|349.00|headphones|1000|Producto de calidad con materiales seleccionados y garantía de origen.
electronica|ELE-TAB-IPADAIR|iPad Air M2|Tablet de 11 pulgadas con chip M2 y compatibilidad con Apple Pencil.|599.00|tablet|1000|Producto de calidad con materiales seleccionados y garantía de origen.
ropa-y-accesorios|ROP-CAMISETA|Camiseta básica de algodón|Camiseta 100% algodón de corte regular, disponible en varios colores.|12.50|tshirt|1000|Producto de calidad con materiales seleccionados y garantía de origen.
ropa-y-accesorios|ROP-JEAN|Pantalón jean recto|Jean de mezclilla con corte recto y lavado clásico.|39.90|jeans|1000|Producto de calidad con materiales seleccionados y garantía de origen.
ropa-y-accesorios|ROP-RUNNING|Zapatillas para correr|Calzado ligero con amortiguación y suela antideslizante.|64.99|sneakers|1000|Producto de calidad con materiales seleccionados y garantía de origen.
ropa-y-accesorios|ROP-RELOJ|Reloj clásico de pulsera|Reloj analógico con correa de cuero y resistencia al agua de 3 ATM.|89.00|watch|1000|Producto de calidad con materiales seleccionados y garantía de origen.
hogar|HOG-SOFA3|Sofá de 3 plazas|Sofá tapizado en tela con estructura de madera y cojines desmontables.|499.00|sofa|1000|Producto de calidad con materiales seleccionados y garantía de origen.
hogar|HOG-LAMPARA|Lámpara de mesa LED|Lámpara regulable de 3 tonos de luz con base de metal.|29.90|lamp|1000|Producto de calidad con materiales seleccionados y garantía de origen.
hogar|HOG-SARTEN|Sartén antiadherente 28 cm|Sartén de aluminio con recubrimiento antiadherente, apta para inducción.|24.50|pan|1000|Producto de calidad con materiales seleccionados y garantía de origen.
hogar|HOG-CUCHILLOS|Set de cuchillos de cocina|Set de 5 cuchillos de acero inoxidable con soporte de madera.|45.00|knives|1000|Producto de calidad con materiales seleccionados y garantía de origen.
alimentos|ALI-CAFE500|Café de origen 500 g|Café molido de origen nacional, tueste medio.|9.80|coffee|1000|Producto de calidad con materiales seleccionados y garantía de origen.
alimentos|ALI-GRANOLA|Granola orgánica 400 g|Avena, frutos secos y miel, sin azúcar añadida.|6.50|granola|1000|Producto de calidad con materiales seleccionados y garantía de origen.
alimentos|ALI-JUGO|Jugo natural de naranja 1 L|Jugo 100% natural, sin conservantes ni azúcar añadida.|3.20|juice|1000|Producto de calidad con materiales seleccionados y garantía de origen.
alimentos|ALI-MIX|Mix de snacks saludables|Mezcla de frutos secos y semillas en presentación familiar.|7.90|nuts|1000|Producto de calidad con materiales seleccionados y garantía de origen.
belleza-y-salud|BEL-PERFUME|Perfume floral 100 ml|Fragancia floral de larga duración.|58.00|perfume|1000|Producto de calidad con materiales seleccionados y garantía de origen.
belleza-y-salud|BEL-LABIAL|Labial mate|Labial de acabado mate, color intenso y larga duración.|14.50|lipstick|1000|Producto de calidad con materiales seleccionados y garantía de origen.
belleza-y-salud|BEL-VITC|Vitamina C 1000 mg|Suplemento de 60 cápsulas de vitamina C para el sistema inmune.|11.90|vitamins|1000|Producto de calidad con materiales seleccionados y garantía de origen.
belleza-y-salud|BEL-CREMA|Crema hidratante facial|Crema ligera de uso diario con ácido hialurónico.|17.80|skincare|1000|Producto de calidad con materiales seleccionados y garantía de origen.
deportes|DEP-BALON|Balón de fútbol N.º 5|Balón oficial de cuero sintético con costura reforzada.|19.90|soccer|1000|Producto de calidad con materiales seleccionados y garantía de origen.
deportes|DEP-BICI|Bicicleta de montaña 29"|Cuadro de aluminio y 21 velocidades para terreno variado.|320.00|bicycle|1000|Producto de calidad con materiales seleccionados y garantía de origen.
deportes|DEP-MANCUERNAS|Set de mancuernas 10 kg|Par de mancuernas ajustables de hasta 10 kg.|42.00|dumbbells|1000|Producto de calidad con materiales seleccionados y garantía de origen.
deportes|DEP-YOGA|Tapete de yoga antideslizante|Tapete de 6 mm con superficie antideslizante.|22.00|yoga|1000|Producto de calidad con materiales seleccionados y garantía de origen.
libros-y-educacion|LIB-SISDIST|Sistemas distribuidos|Edición universitaria sobre comunicación, consenso y tolerancia a fallos.|45.00|books|1000|Producto de calidad con materiales seleccionados y garantía de origen.
libros-y-educacion|LIB-PYTHON|E-book de programación Python|Guía digital para aprender Python desde cero.|9.99|ebook|1000|Producto de calidad con materiales seleccionados y garantía de origen.
libros-y-educacion|LIB-JAVA|Curso en línea de Java|Curso con 30 horas de video y ejercicios prácticos.|29.00|coding|1000|Producto de calidad con materiales seleccionados y garantía de origen.
libros-y-educacion|LIB-CUADERNO|Cuaderno universitario A4|Cuaderno de 200 hojas con tapa dura y cuadrícula.|4.50|notebook|1000|Producto de calidad con materiales seleccionados y garantía de origen.
videojuegos|VID-PS5|Consola PlayStation 5|Consola con SSD de 825 GB y mando DualSense.|499.00|playstation|1000|Producto de calidad con materiales seleccionados y garantía de origen.
videojuegos|VID-JUEGO|Juego de acción y aventura|Edición estándar de videojuego de mundo abierto.|59.99|videogame|1000|Producto de calidad con materiales seleccionados y garantía de origen.
videojuegos|VID-CONTROL|Control inalámbrico gamer|Mando inalámbrico compatible con PC y consola.|49.00|gamepad|1000|Producto de calidad con materiales seleccionados y garantía de origen.
videojuegos|VID-AUDIFONOS|Audífonos gamer con micrófono|Sonido envolvente y micrófono retráctil.|39.00|gaming-headset|1000|Producto de calidad con materiales seleccionados y garantía de origen.
mascotas|MAS-COMIDA|Alimento para perro adulto 3 kg|Alimento balanceado para perros adultos de todas las razas.|18.50|dog-food|1000|Producto de calidad con materiales seleccionados y garantía de origen.
mascotas|MAS-JUGUETE|Juguete de cuerda para gato|Juguete interactivo con plumas que estimula el instinto de caza.|5.90|cat-toy|1000|Producto de calidad con materiales seleccionados y garantía de origen.
mascotas|MAS-CAMA|Cama para mascota mediana|Cama acolchada y lavable para perros y gatos medianos.|35.00|pet-bed|1000|Producto de calidad con materiales seleccionados y garantía de origen.
mascotas|MAS-CORREA|Correa retráctil 5 m|Correa retráctil con freno y agarre ergonómico.|14.00|dog-leash|1000|Producto de calidad con materiales seleccionados y garantía de origen.
automotriz|AUT-ACEITE|Aceite sintético 5W-30 4 L|Aceite de motor sintético para motores a gasolina.|32.00|motor-oil|1000|Producto de calidad con materiales seleccionados y garantía de origen.
automotriz|AUT-LLANTA|Llanta 205/55 R16|Llanta para uso urbano con buen agarre en mojado.|79.00|tire|1000|Producto de calidad con materiales seleccionados y garantía de origen.
automotriz|AUT-KIT|Kit de herramientas de emergencia|Gato hidráulico, llave de cruz y cables de arranque.|49.90|car-tools|1000|Producto de calidad con materiales seleccionados y garantía de origen.
automotriz|AUT-CAMARA|Cámara de retroceso HD|Cámara con visión nocturna para asistir al estacionar.|26.00|car-camera|1000|Producto de calidad con materiales seleccionados y garantía de origen.
DATOS
)

# Usa una imagen local únicamente si su contenido corresponde a una imagen real.
imagen_local() { # $1 SKU, $2 destino
  local origen extension tipo
  for extension in jpg jpeg png webp; do
    origen="$(dirname "$0")/imagenes/$1.$extension"
    [ -f "$origen" ] || continue
    tipo="$(file -b --mime-type "$origen")"
    case "$tipo" in image/jpeg|image/png|image/webp) cp "$origen" "$2"; return 0 ;; esac
    echo "  imagen local rechazada para $1: tipo $tipo" >&2
  done
  return 1
}

# Conserva la validación para cualquier archivo local.
validar_imagen() {
  case "$(file -b --mime-type "$2")" in
    image/jpeg|image/png|image/webp) [ "$(stat -c '%s' "$2")" -gt 10240 ] && return 0 ;;
  esac
  rm -f "$2"
  return 1
}

# Respaldo claro con icono textual de categoría y nombre legible.
generar_respaldo() { # $1 nombre, $2 categoría, $3 destino
  [ -n "$IM" ] || return 1
  "$IM" -size 800x800 xc:'#fffaf5' -fill '#ff7a1a' -draw 'circle 400,190 400,110' \
    -font "$FUENTE" -pointsize 42 -gravity center -annotate +0+80 "$2" \
    -fill '#333333' -pointsize 38 -annotate +0+180 "$1" \
    -strip -quality 90 "$3"
  [ -f "$3" ] && [ "$(stat -c '%s' "$3")" -gt 10240 ]
}

# Sube la imagen a un producto y devuelve el código HTTP
subir_imagen() { # $1 id del producto, $2 archivo
  curl -s -o /dev/null -w '%{http_code}' \
    -F "archivo=@$2;type=$(file -b --mime-type "$2")" "$API/productos/$1/imagenes"
}

echo "Gateway: $GATEWAY"
curl -fs -o /dev/null "$API/health" || { echo "No responde el catálogo en $GATEWAY" >&2; exit 1; }

# Mapas: slug -> id de categoría, y SKU -> id de producto ya existente
declare -A CATEGORIAS CATEGORIA_NOMBRE PRODUCTOS_EXISTENTES
while IFS=$'\t' read -r slug id nombre; do CATEGORIAS[$slug]=$id; CATEGORIA_NOMBRE[$slug]=$nombre; done < <(curl -fs "$API/categorias" | jq -r '.[] | [.slug, .id, .nombre] | @tsv')
pagina=0
while :; do
  respuesta=$(curl -fs "$API/productos?pagina=$pagina&tamanio=100")
  while IFS=$'\t' read -r sku id; do PRODUCTOS_EXISTENTES[$sku]=$id; done < <(jq -r '.contenido[] | [.sku, .id] | @tsv' <<<"$respuesta")
  [ "$(jq -r '.ultima' <<<"$respuesta")" = "true" ] && break
  pagina=$((pagina + 1))
done

creados=0; existentes=0; con_foto=0; con_respaldo=0; sin_foto=0

especificaciones_categoria() {
  case "$1" in
    electronica) echo '{"Pantalla":"alta resolución","Almacenamiento":"256 GB","Batería":"larga duración","Conectividad":"Wi-Fi y Bluetooth"}' ;;
    ropa-y-accesorios) echo '{"Material":"algodón y fibras resistentes","Talla":"varias tallas","Cuidado":"lavado delicado","Color":"seleccionado por modelo"}' ;;
    hogar) echo '{"Material":"material durable","Uso":"hogar","Mantenimiento":"limpieza sencilla","Garantía":"12 meses"}' ;;
    alimentos) echo '{"Contenido":"presentación indicada","Origen":"selección nacional","Conservación":"lugar fresco y seco","Alérgenos":"revisar etiqueta"}' ;;
    belleza-y-salud) echo '{"Contenido":"presentación indicada","Uso":"uso diario","Piel":"apto según etiqueta","Conservación":"lugar fresco y seco"}' ;;
    deportes) echo '{"Material":"material resistente","Uso":"entrenamiento","Nivel":"uso recreativo","Garantía":"12 meses"}' ;;
    libros-y-educacion) echo '{"Formato":"tapa o digital según producto","Idioma":"español","Nivel":"inicial a universitario","Contenido":"educativo"}' ;;
    videojuegos) echo '{"Formato":"edición estándar","Plataforma":"compatible según producto","Idioma":"español","Edad":"consultar clasificación"}' ;;
    mascotas) echo '{"Material":"seguro para mascotas","Uso":"hogar","Tamaño":"varios tamaños","Cuidado":"limpieza sencilla"}' ;;
    automotriz) echo '{"Material":"uso automotriz","Compatibilidad":"consultar modelo","Instalación":"manual incluida","Garantía":"12 meses"}' ;;
    *) echo '{"Calidad":"seleccionada","Uso":"diario","Garantía":"12 meses"}' ;;
  esac
}

exec 3<<<"$PRODUCTOS"
while IFS='|' read -r -u 3 slug sku nombre descripcion precio clave peso especificaciones; do
  [ -n "$slug" ] || continue
  categoria_id=${CATEGORIAS[$slug]:-}
  if [ -z "$categoria_id" ]; then echo "  categoría desconocida '$slug' para $sku; se omite" >&2; continue; fi

  id=${PRODUCTOS_EXISTENTES[$sku]:-}
  descripcion_real="$descripcion Producto seleccionado para una compra confiable y uso diario."
  especificaciones_real="$(especificaciones_categoria "$slug")"
  cuerpo=$(jq -n --arg nombre "$nombre" --arg descripcion "$descripcion_real" --arg sku "$sku" \
    --arg especificaciones "$especificaciones_real" \
    --argjson precio "$precio" --argjson stock "$STOCK_INICIAL" --argjson categoriaId "$categoria_id" \
    --argjson peso "$peso" \
    '{nombre:$nombre, descripcion:$descripcion, pesoGramos:$peso, especificaciones:$especificaciones, precio:$precio, stock:$stock, sku:$sku, activo:true, categoriaId:$categoriaId}')
  if [ -z "$id" ]; then
    id=$(curl -s -X POST -H 'Content-Type: application/json' -d "$cuerpo" "$API/productos" | jq -r '.id // empty')
    if [ -z "$id" ]; then echo "  no se pudo crear $sku" >&2; continue; fi
    creados=$((creados + 1))
    echo "+ $sku  $nombre"
  else
    existentes=$((existentes + 1))
    # Actualiza por SKU para completar también productos cargados por versiones anteriores.
    curl -fs -X PUT -H 'Content-Type: application/json' -d "$cuerpo" "$API/productos/$id" >/dev/null
    imagenes_actuales=$(curl -fs "$API/productos/$id/imagenes")
    if [ "$REIMAGENES" = true ]; then
      while IFS= read -r imagen_id; do
        [ -n "$imagen_id" ] || continue
        curl -fs -X DELETE "$API/productos/$id/imagenes/$imagen_id" >/dev/null
      done < <(jq -r '.[].id' <<<"$imagenes_actuales")
    elif [ "$(jq 'length' <<<"$imagenes_actuales")" -gt 0 ]; then
      continue
    fi
  fi

  foto="$TMP/$sku.jpg"
  if imagen_local "$sku" "$foto" && validar_imagen "$sku" "$foto"; then
    origen="foto local"; con_foto=$((con_foto + 1))
  elif generar_respaldo "$nombre" "${CATEGORIA_NOMBRE[$slug]}" "$foto"; then
    origen="respaldo"; con_respaldo=$((con_respaldo + 1))
  else
    echo "  sin imagen para $sku" >&2; sin_foto=$((sin_foto + 1)); continue
  fi

  codigo=$(subir_imagen "$id" "$foto")
  if [ "$codigo" = "201" ]; then
    echo "  imagen ($origen) subida a $sku"
  else
    echo "  la imagen de $sku fue rechazada (HTTP $codigo)" >&2; sin_foto=$((sin_foto + 1))
  fi
done

echo "Listo. Creados: $creados · Ya existían: $existentes · Fotos libres: $con_foto · Respaldos: $con_respaldo · Sin imagen: $sin_foto"
