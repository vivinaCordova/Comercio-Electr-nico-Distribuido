import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Encabezado from '../components/layout/Encabezado.jsx';
import { useCarrito } from '../context/CarritoContext';
import { PROMO_SELL12, aCentavos, formatoMoneda } from '../utils/dinero';
import imagenProducto from '../assets/imagen-producto.svg';

export default function CartPage() {
  const navigate = useNavigate();
  const { items, totales, promo, cargando, error, hayNoDisponibles, cambiarCantidad, quitar, vaciar, aplicarPromo, quitarPromo } = useCarrito();
  const [codigo, setCodigo] = useState('');
  const [promoMensaje, setPromoMensaje] = useState(null);

  const aplicar = (event) => {
    event.preventDefault();
    const ok = aplicarPromo(codigo);
    setPromoMensaje(ok ? null : { type: 'error', text: `El código no es válido. Prueba con ${PROMO_SELL12}.` });
    if (ok) setCodigo('');
  };

  return (
    <div className="shop-shell">
      <Encabezado />

      <div className="shop-content">
        <section className="products-column">
          <div className="section-heading">
            <h1>Mi carrito</h1>
            {items.length > 0 && <button className="boton-texto" onClick={vaciar}>Vaciar carrito</button>}
          </div>

          {cargando && <p className="empty-cart">Cargando carrito...</p>}

          {!cargando && !items.length && (
            <div className="estado-vacio">
              <h2>Tu carrito está vacío</h2>
              <p>Explora los productos y agrégalos aquí.</p>
              <Link className="boton-naranja" to="/productos">Ver productos</Link>
            </div>
          )}

          <ul className="carrito-lista">
            {items.map((item) => (
              <li className={`carrito-linea${item.noDisponible ? ' no-disponible' : ''}`} key={item.id}>
                <div className="carrito-imagen">
                  <img src={item.imagen || imagenProducto} alt={item.nombre} loading="lazy" onError={(event) => { event.currentTarget.src = imagenProducto; }} />
                </div>
                <div className="carrito-info">
                  <h2>{item.nombre}</h2>
                  {item.noDisponible
                    ? <span className="aviso">No disponible: quítalo para continuar</span>
                    : <small>{formatoMoneda(aCentavos(item.precio))} c/u · {item.stock} en stock</small>}
                </div>
                <div className="quantity-control">
                  <button aria-label={`Disminuir ${item.nombre}`} onClick={() => cambiarCantidad(item, item.cantidad - 1)} disabled={item.noDisponible}>−</button>
                  <span>{item.cantidad}</span>
                  <button aria-label={`Aumentar ${item.nombre}`} onClick={() => cambiarCantidad(item, item.cantidad + 1)} disabled={item.noDisponible || item.cantidad >= item.stock}>＋</button>
                </div>
                <b className="carrito-subtotal">{item.noDisponible ? '—' : formatoMoneda(aCentavos(item.precio) * item.cantidad)}</b>
                <button className="quitar" onClick={() => quitar(item)}>Quitar</button>
              </li>
            ))}
          </ul>
        </section>

        <aside className="cart-panel resumen">
          <div className="cart-title"><h1>Resumen</h1></div>

          <form className="promo-form" onSubmit={aplicar}>
            <label>Código promocional
              <input value={codigo} onChange={(event) => setCodigo(event.target.value)} placeholder={PROMO_SELL12} />
            </label>
            <button type="submit">Aplicar</button>
          </form>
          {promo && <p className="order-message success promo-activa">Promo {PROMO_SELL12} aplicada (12%) <button className="boton-texto" onClick={quitarPromo}>Quitar</button></p>}
          {promoMensaje && <p className={`order-message ${promoMensaje.type}`}>{promoMensaje.text}</p>}

          <div className="summary">
            <div><span>Subtotal</span><b>{formatoMoneda(totales.subtotal)}</b></div>
            <div><span>Descuento</span><b className="discount">−{formatoMoneda(totales.descuento)}</b></div>
            <div className="total-row"><span>Total a pagar</span><b>{formatoMoneda(totales.total)}</b></div>
          </div>

          {hayNoDisponibles && <p className="order-message error">Hay productos no disponibles en tu carrito. Quítalos para pagar.</p>}
          {error && <p className="order-message error">{error}</p>}
          <button className="payment-button" onClick={() => navigate('/checkout')} disabled={!items.length || hayNoDisponibles}>Proceder al pago</button>
        </aside>
      </div>
    </div>
  );
}
