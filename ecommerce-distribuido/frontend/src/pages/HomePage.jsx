import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import Encabezado from '../components/layout/Encabezado.jsx';
import ChipsCategorias from '../components/catalogo/ChipsCategorias.jsx';
import IconoCategoria from '../components/catalogo/IconoCategoria.jsx';
import { useCarrito } from '../context/CarritoContext';
import { catalogoService } from '../services/catalogoService';
import { textoError } from '../utils/errores';
import TarjetaProducto from '../components/products/TarjetaProducto.jsx';

// Mensaje de estado vacío según los filtros activos
const mensajeVacio = (categoria, busqueda) => {
  if (busqueda && categoria) return 'No hay productos que coincidan con tu búsqueda en esta categoría.';
  if (categoria) return 'No hay productos en esta categoría.';
  if (busqueda) return `No encontramos productos para «${busqueda}».`;
  return 'Aún no hay productos disponibles.';
};

export default function HomePage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const categoria = searchParams.get('categoria') || '';
  const { cantidadTotal, error, setError, agregar } = useCarrito();
  const [categorias, setCategorias] = useState([]);
  const [productos, setProductos] = useState([]);
  const [query, setQuery] = useState(searchParams.get('q') || '');
  const [busqueda, setBusqueda] = useState('');
  const [cargando, setCargando] = useState(true);

  // La búsqueda espera un momento antes de consultar al servidor
  useEffect(() => {
    const timer = setTimeout(() => setBusqueda(query.trim()), 300);
    return () => clearTimeout(timer);
  }, [query]);
  useEffect(() => { setQuery(searchParams.get('q') || ''); }, [searchParams]);

  useEffect(() => {
    catalogoService.categorias().then(setCategorias).catch(() => setCategorias([]));
  }, []);

  // Ignora respuestas viejas si el usuario cambia de filtro mientras se carga
  useEffect(() => {
    let vigente = true;
    setCargando(true);
    catalogoService.listar({ categoria, q: busqueda })
      .then((lista) => { if (vigente) setProductos(lista); })
      .catch((e) => { if (vigente) setError(textoError(e, 'No se pudo cargar la tienda. Verifica ms-catalogo y el Gateway.')); })
      .finally(() => { if (vigente) setCargando(false); });
    return () => { vigente = false; };
  }, [categoria, busqueda]);

  const elegirCategoria = (slug) => setSearchParams(slug ? { categoria: slug } : {});
  const categoriaActiva = categorias.find((c) => c.slug === categoria);
  const total = categorias.reduce((suma, c) => suma + c.cantidadProductos, 0);

  return (
    <div className="shop-shell">
      <Encabezado />

      <div className="shop-content una-columna">
        <section className="products-column" id="products">
          <div className="section-heading">
            <div>
              <h1 className="titulo-categoria">
                {categoriaActiva && <IconoCategoria clave={categoriaActiva.icono} className="icono-titulo" />}
                {categoriaActiva ? categoriaActiva.nombre : 'Productos'}
              </h1>
              {categoriaActiva && <p className="subtitulo">{categoriaActiva.descripcion}</p>}
            </div>
            <div className="heading-actions">
              <label className="search-box">
                <span aria-hidden="true">⌕</span>
                <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar productos" aria-label="Buscar productos" />
              </label>
              <button className="create-button" onClick={() => navigate('/carrito')}>Ver carrito ({cantidadTotal})</button>
            </div>
          </div>

          <ChipsCategorias categorias={categorias} activa={categoria} total={total} onElegir={elegirCategoria} />

          {error && <p className="order-message error">{error}</p>}
          {cargando && <p className="empty-cart">Cargando productos...</p>}

          {!cargando && !productos.length && (
            <div className="estado-vacio">
              <h2>{mensajeVacio(categoria, busqueda)}</h2>
              {(categoria || busqueda) && (
                <button className="boton-naranja" onClick={() => { setQuery(''); elegirCategoria(''); }}>Ver todos los productos</button>
              )}
            </div>
          )}

          <div className="product-grid">
            {!cargando && productos.map((product) => (
              <TarjetaProducto key={product.id} producto={product} onAgregar={agregar} />
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
