import React, { useEffect, useMemo, useState } from 'react';
import Encabezado from '../components/layout/Encabezado.jsx';
import { useAuth } from '../context/AuthContext';
import { catalogoService } from '../services/catalogoService';
import { carritoService } from '../services/carritoService';
import { ordenesService } from '../services/ordenesService';

const money = (value) => `$${Number(value).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
const textoError = (error, porDefecto) => error.response?.data?.error || error.response?.data?.message || porDefecto;

export default function HomePage() {
  const { usuario } = useAuth();
  const [productos, setProductos] = useState([]);
  const [items, setItems] = useState([]); // [{ productoId, cantidad }] guardado en ms-carrito
  const [query, setQuery] = useState('');
  const [promo, setPromo] = useState('');
  const [message, setMessage] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    Promise.all([catalogoService.listar(), carritoService.obtener(usuario.id)])
      .then(([lista, carrito]) => { setProductos(lista); setItems(carrito.items); })
      .catch((error) => setMessage({ type: 'error', text: textoError(error, 'No se pudo cargar la tienda. Verifica ms-catalogo, ms-carrito y el Gateway.') }))
      .finally(() => setCargando(false));
  }, [usuario.id]);

  const visibleProducts = useMemo(() => productos.filter((p) => p.nombre.toLowerCase().includes(query.toLowerCase())), [productos, query]);
  const cart = items.map((i) => ({ ...productos.find((p) => p.id === i.productoId), cantidad: i.cantidad })).filter((i) => i.id);

  const subtotal = cart.reduce((total, item) => total + item.precio * item.cantidad, 0);
  const discount = promo.trim().toUpperCase() === 'SELL12' ? Math.round(subtotal * 12) / 100 : 0;
  const total = subtotal - discount;

  // Cada acción se guarda en ms-carrito y la pantalla se actualiza con su respuesta
  const actualizar = (peticion) => peticion
    .then((carrito) => setItems(carrito.items))
    .catch((error) => setMessage({ type: 'error', text: textoError(error, 'No se pudo actualizar el carrito.') }));
  const addToCart = (producto) => actualizar(carritoService.agregar(usuario.id, producto.id, 1));
  const changeQuantity = (item, cantidad) => actualizar(cantidad > 0
    ? carritoService.cambiar(usuario.id, item.id, cantidad)
    : carritoService.quitar(usuario.id, item.id));
  const clearCart = () => carritoService.vaciar(usuario.id).then(() => setItems([]))
    .catch((error) => setMessage({ type: 'error', text: textoError(error, 'No se pudo vaciar el carrito.') }));

  const createOrder = async () => {
    if (!cart.length) {
      setMessage({ type: 'error', text: 'Agrega al menos un producto al carrito.' });
      return;
    }
    setSubmitting(true);
    setMessage(null);
    try {
      await ordenesService.crear({
        userId: usuario.id,
        items: cart.map(({ id, cantidad, precio }) => ({ productId: id, quantity: cantidad, precioEsperado: precio })),
      });
      await carritoService.vaciar(usuario.id);
      setItems([]);
      setMessage({ type: 'success', text: 'Orden creada correctamente.' });
    } catch (error) {
      setMessage({ type: 'error', text: textoError(error, 'No se pudo crear la orden. Verifica ms-ordenes y ms-catalogo.') });
    } finally {
      setSubmitting(false);
    }
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
              <button className="create-button" onClick={() => document.getElementById('cart')?.scrollIntoView({ behavior: 'smooth' })}>＋ Crear orden</button>
            </div>
          </div>
          {cargando && <p className="empty-cart">Cargando productos...</p>}
          {!cargando && !visibleProducts.length && <p className="empty-cart">No hay productos para mostrar.</p>}
          <div className="product-grid">
            {visibleProducts.map((product) => <article className="product-card" key={product.id}>
              <div className="product-image">{product.imagen && <img src={product.imagen} alt={product.nombre} />}</div>
              <h2>{product.nombre}</h2>
              <div className="product-footer"><div><strong>{money(product.precio)}</strong></div><button className="add-button" onClick={() => addToCart(product)} disabled={product.stock < 1} aria-label={`Agregar ${product.nombre}`}>↗</button></div>
            </article>)}
          </div>
        </section>

        <aside className="cart-panel" id="cart">
          <div className="cart-title"><h1>Carrito</h1><button onClick={clearCart}>Vaciar</button></div>
          <div className="cart-items">
            {cart.length ? cart.map((item) => <div className="cart-item" key={item.id}>
              <span className="selected">✓</span><img src={item.imagen} alt="" /><div className="cart-item-info"><strong>{item.nombre}</strong><small>{item.sku}</small><div className="quantity"><button onClick={() => changeQuantity(item, item.cantidad - 1)}>−</button><span>{item.cantidad}</span><button onClick={() => changeQuantity(item, item.cantidad + 1)}>＋</button></div></div><button className="remove-item" onClick={() => changeQuantity(item, 0)} aria-label={`Eliminar ${item.nombre}`}>♧</button><b>{money(item.precio * item.cantidad)}</b>
            </div>) : <p className="empty-cart">Tu carrito está vacío.</p>}
          </div>
          <div className="promo"><span>◉ &nbsp; Promo para todos (12%)</span><input value={promo} onChange={(event) => setPromo(event.target.value)} placeholder="SELL12" /><button onClick={() => setPromo('SELL12')}>Aplicar</button></div>
          <div className="summary"><div><span>Total productos</span><b>{money(subtotal)}</b></div><div><span>Descuento</span><b className="discount">−{money(discount)}</b></div><div className="total-row"><span>Total a pagar</span><b>{money(total)}</b></div></div>
          {message && <p className={`order-message ${message.type}`}>{message.text}</p>}
          <button className="payment-button" onClick={createOrder} disabled={submitting}>{submitting ? 'Creando orden...' : 'Proceder al pago'}</button>
        </aside>
      </div>
    </div>
  );
}
