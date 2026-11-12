import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Encabezado from '../components/layout/Encabezado.jsx';
import { useAuth } from '../context/AuthContext';
import { useCarrito } from '../context/CarritoContext';
import { ordenesService } from '../services/ordenesService';
import { aCentavos, formatoMoneda } from '../utils/dinero';
import { textoError } from '../utils/errores';

export default function CheckoutPage() {
  const { usuario } = useAuth();
  const navigate = useNavigate();
  const { items, totales, promo, cargando, hayNoDisponibles, vaciar } = useCarrito();
  const [enviando, setEnviando] = useState(false);
  const [error, setError] = useState(null);

  // Envía exactamente los ítems y la promo que se muestran en la factura
  const confirmar = async () => {
    setEnviando(true);
    setError(null);
    let orden;
    try {
      orden = await ordenesService.crear({
        userId: usuario.id,
        items: items.map(({ id, cantidad, precio }) => ({ productId: id, quantity: cantidad, precioEsperado: precio })),
        promo: promo || null,
      });
    } catch (e) {
      setError(textoError(e, 'No se pudo crear la orden. Verifica ms-ordenes y ms-catalogo.'));
      setEnviando(false);
      return;
    }
    const vaciado = await vaciar();
    const aviso = vaciado ? '' : ' Sin embargo, no se pudo vaciar el carrito.';
    navigate('/ordenes', {
      state: { tipo: vaciado ? 'success' : 'error', mensaje: `Orden #${orden.id} creada por ${formatoMoneda(aCentavos(orden.total))}. Su estado se actualizará en unos segundos.${aviso}` },
    });
  };

  if (cargando) return <div className="shop-shell"><Encabezado /><p className="empty-cart">Cargando factura...</p></div>;

  return (
    <div className="shop-shell">
      <Encabezado />

      <div className="shop-content">
        <section className="products-column">
          <div className="section-heading">
            <h1>Factura</h1>
            <Link className="volver" to="/carrito">← Volver al carrito</Link>
          </div>
          <p className="cliente">Cliente: <strong>{usuario?.nombre}</strong> ({usuario?.email})</p>

          {!items.length ? (
            <div className="estado-vacio">
              <h2>Tu carrito está vacío</h2>
              <p>Agrega productos antes de pagar.</p>
              <Link className="boton-naranja" to="/productos">Ver productos</Link>
            </div>
          ) : (
            <div className="tabla-scroll">
              <table className="factura">
                <thead>
                  <tr><th>Producto</th><th>Cantidad</th><th>Precio unitario</th><th>Subtotal</th></tr>
                </thead>
                <tbody>
                  {items.map((item) => (
                    <tr key={item.id} className={item.noDisponible ? 'no-disponible' : ''}>
                      <td>{item.nombre}{item.noDisponible && <small> (no disponible)</small>}</td>
                      <td>{item.cantidad}</td>
                      <td>{formatoMoneda(aCentavos(item.precio))}</td>
                      <td>{item.noDisponible ? '—' : formatoMoneda(aCentavos(item.precio) * item.cantidad)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <aside className="cart-panel resumen">
          <div className="cart-title"><h1>Total</h1></div>
          <div className="summary">
            <div><span>Subtotal</span><b>{formatoMoneda(totales.subtotal)}</b></div>
            <div><span>Descuento {promo ? '(SELL12 12%)' : ''}</span><b className="discount">−{formatoMoneda(totales.descuento)}</b></div>
            <div className="total-row"><span>Total a pagar</span><b>{formatoMoneda(totales.total)}</b></div>
          </div>
          {hayNoDisponibles && <p className="order-message error">Hay productos no disponibles en tu carrito. Quítalos antes de pagar.</p>}
          {error && <p className="order-message error">{error}</p>}
          <button className="payment-button" onClick={confirmar} disabled={enviando || !items.length || hayNoDisponibles}>
            {enviando ? 'Creando orden...' : 'Confirmar compra'}
          </button>
        </aside>
      </div>
    </div>
  );
}
