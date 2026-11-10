import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Encabezado from '../components/layout/Encabezado.jsx';
import { useCarrito } from '../context/CarritoContext';
import { catalogoService } from '../services/catalogoService';
import { formatoMoneda, aCentavos } from '../utils/dinero';
import { textoError } from '../utils/errores';

export default function HomePage() {
  const navigate = useNavigate();
  const { cantidadTotal, error, setError, agregar } = useCarrito();
  const [productos, setProductos] = useState([]);
  const [query, setQuery] = useState('');
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    catalogoService.listar()
      .then(setProductos)
      .catch((e) => setError(textoError(e, 'No se pudo cargar la tienda. Verifica ms-catalogo y el Gateway.')))
      .finally(() => setCargando(false));
  }, []);

  const visibleProducts = useMemo(() => productos.filter((p) => p.nombre.toLowerCase().includes(query.toLowerCase())), [productos, query]);

  return (
    <div className="shop-shell">
      <Encabezado />

      <div className="shop-content una-columna">
        <section className="products-column" id="products">
          <div className="section-heading">
            <h1>Productos</h1>
            <div className="heading-actions">
              <label className="search-box">⌕<input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar..." /></label>
              <button className="create-button" onClick={() => navigate('/carrito')}>Ver carrito ({cantidadTotal})</button>
            </div>
          </div>
          {error && <p className="order-message error">{error}</p>}
          {cargando && <p className="empty-cart">Cargando productos...</p>}
          {!cargando && !visibleProducts.length && <p className="empty-cart">No hay productos para mostrar.</p>}
          <div className="product-grid">
            {visibleProducts.map((product) => <article className="product-card" key={product.id}>
              <div className="product-image">{product.imagen && <img src={product.imagen} alt={product.nombre} />}</div>
              <h2>{product.nombre}</h2>
              <div className="product-footer"><div><strong>{formatoMoneda(aCentavos(product.precio))}</strong></div><button className="add-button" onClick={() => agregar(product)} disabled={product.stock < 1} aria-label={`Agregar ${product.nombre}`}>↗</button></div>
            </article>)}
          </div>
        </section>
      </div>
    </div>
  );
}
