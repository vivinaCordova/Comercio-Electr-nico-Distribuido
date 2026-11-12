import React, { useCallback, useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import Encabezado from '../components/layout/Encabezado.jsx';
import { useAuth } from '../context/AuthContext';
import { catalogoService } from '../services/catalogoService';
import { ordenesService } from '../services/ordenesService';
import { aCentavos, formatoMoneda } from '../utils/dinero';
import { textoError } from '../utils/errores';

const ETIQUETAS = { PENDIENTE: 'Pendiente', CONFIRMADA: 'Confirmada', CANCELADA: 'Cancelada' };

const fechaLegible = (fecha) => (fecha
  ? new Date(fecha).toLocaleString('es-EC', { dateStyle: 'medium', timeStyle: 'short' })
  : 'Sin fecha');

export default function OrdersPage() {
  const { usuario } = useAuth();
  const { state } = useLocation();
  const [ordenes, setOrdenes] = useState([]);
  const [nombres, setNombres] = useState({});
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);

  const cargar = useCallback(() => {
    return Promise.all([ordenesService.listar(usuario.id), catalogoService.listar().catch(() => [])])
      .then(([lista, productos]) => {
        setOrdenes(lista);
        setNombres(Object.fromEntries(productos.map((p) => [p.id, p.nombre])));
        setError(null);
      })
      .catch((e) => setError(textoError(e, 'No se pudo cargar tus órdenes. Verifica ms-ordenes y el Gateway.')))
      .finally(() => setCargando(false));
  }, [usuario.id]);

  useEffect(() => { cargar(); }, [cargar]);

  // Mientras haya órdenes PENDIENTE, se refresca solo hasta que el evento de stock las confirme o cancele
  useEffect(() => {
    if (!ordenes.some((o) => o.estado === 'PENDIENTE')) return undefined;
    const timer = setTimeout(cargar, 2000);
    return () => clearTimeout(timer);
  }, [ordenes, cargar]);

  return (
    <div className="shop-shell">
      <Encabezado />

      <div className="shop-content una-columna">
        <section className="products-column">
          <div className="section-heading">
            <h1>Mis órdenes</h1>
            <button className="create-button" onClick={cargar}>Actualizar</button>
          </div>

          {state?.mensaje && <p className={`order-message ${state.tipo || 'success'}`}>{state.mensaje}</p>}
          {error && <p className="order-message error">{error}</p>}
          {cargando && <p className="empty-cart">Cargando órdenes...</p>}

          {!cargando && !error && !ordenes.length && (
            <div className="estado-vacio">
              <h2>Aún no tienes órdenes</h2>
              <p>Cuando completes una compra, aparecerá aquí.</p>
              <Link className="boton-naranja" to="/productos">Ir a productos</Link>
            </div>
          )}

          <div className="ordenes-lista">
            {ordenes.map((orden) => (
              <article className="orden-card" key={orden.id}>
                <header className="orden-cabecera">
                  <div>
                    <h2>Orden #{orden.id}</h2>
                    <small>{fechaLegible(orden.fecha)}</small>
                  </div>
                  <span className={`estado estado-${orden.estado.toLowerCase()}`}>{ETIQUETAS[orden.estado] || orden.estado}</span>
                </header>

                <ul className="orden-items">
                  {orden.items.map((item) => (
                    <li key={item.productoId}>
                      <span>{nombres[item.productoId] || `Producto #${item.productoId}`} × {item.cantidad}</span>
                      <span>{formatoMoneda(aCentavos(item.precioUnitario) * item.cantidad)}</span>
                    </li>
                  ))}
                </ul>

                {orden.estado === 'CANCELADA' && orden.motivo && <p className="motivo">Motivo: {orden.motivo}</p>}

                <footer className="orden-total">
                  {Number(orden.descuento) > 0 && <span>Descuento SELL12: −{formatoMoneda(aCentavos(orden.descuento))}</span>}
                  <b>Total {formatoMoneda(aCentavos(orden.total))}</b>
                </footer>
              </article>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
