import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Encabezado from '../components/layout/Encabezado.jsx';
import { useCarrito } from '../context/CarritoContext';
import { catalogoService } from '../services/catalogoService';
import { formatoMoneda, aCentavos } from '../utils/dinero';
import { textoError } from '../utils/errores';

export default function HomePage() {
  const navigate = useNavigate();
  const { items, totales, promo, cargando: cargandoCarrito, error, setError, agregar, cambiarCantidad, quitar, vaciar, aplicarPromo, quitarPromo } = useCarrito();
  const [productos, setProductos] = useState([]);
  const [query, setQuery] = useState('');
  const [promoInput, setPromoInput] = useState('');
  const [promoMensaje, setPromoMensaje] = useState(null);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    catalogoService.listar()
      .then(setProductos)
      .catch((e) => setError(textoError(e, 'No se pudo cargar la tienda. Verifica ms-catalogo y el Gateway.')))
      .finally(() => setCargando(false));
  }, []);

  const visibleProducts = useMemo(() => productos.filter((p) => p.nombre.toLowerCase().includes(query.toLowerCase())), [productos, query]);

  const aplicar = () => {
    const ok = aplicarPromo(promoInput);
    setPromoMensaje(ok ? { type: 'success', text: 'Promoción SELL12 aplicada (12%).' } : { type: 'error', text: 'El código no es válido.' });
  };

  return (
    <div className="shop-shell">
      <Encabezado />

      <div className="shop-content">
        <section className="products-column" id="products">
          <div className="section-heading">
            <h1>Productos</h1>
            <div className="heading-actions">
              <label className="search-box">⌕<input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar..." /></label>
            </div>
          </div>
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

        <aside className="cart-panel" id="cart">
          <div className="cart-title"><h1>Carrito</h1><button onClick={vaciar}>Vaciar</button></div>
          <div className="cart-items">
            {cargandoCarrito && <p className="empty-cart">Cargando carrito...</p>}
            {!cargandoCarrito && !items.length && <p className="empty-cart">Tu carrito está vacío.</p>}
            {items.map((item) => <div className="cart-item" key={item.id}>
              <span className="selected">✓</span>{item.imagen && <img src={item.imagen} alt="" />}<div className="cart-item-info"><strong>{item.nombre}</strong><small>{item.sku}</small><div className="quantity"><button onClick={() => cambiarCantidad(item, item.cantidad - 1)}>−</button><span>{item.cantidad}</span><button onClick={() => cambiarCantidad(item, item.cantidad + 1)}>＋</button></div></div><button className="remove-item" onClick={() => quitar(item)} aria-label={`Eliminar ${item.nombre}`}>♧</button><b>{formatoMoneda(aCentavos(item.precio) * item.cantidad)}</b>
            </div>)}
          </div>
          <div className="promo"><span>◉ &nbsp; Promo para todos (12%)</span><input value={promoInput} onChange={(event) => setPromoInput(event.target.value)} placeholder="SELL12" /><button onClick={aplicar}>Aplicar</button></div>
          {promo && <button className="quitar-promo" onClick={quitarPromo}>Quitar promoción</button>}
          {promoMensaje && <p className={`order-message ${promoMensaje.type}`}>{promoMensaje.text}</p>}
          <div className="summary"><div><span>Total productos</span><b>{formatoMoneda(totales.subtotal)}</b></div><div><span>Descuento</span><b className="discount">−{formatoMoneda(totales.descuento)}</b></div><div className="total-row"><span>Total a pagar</span><b>{formatoMoneda(totales.total)}</b></div></div>
          {error && <p className="order-message error">{error}</p>}
          <button className="payment-button" onClick={() => navigate('/checkout')} disabled={!items.length}>Proceder al pago</button>
        </aside>
      </div>
    </div>
  );
}
