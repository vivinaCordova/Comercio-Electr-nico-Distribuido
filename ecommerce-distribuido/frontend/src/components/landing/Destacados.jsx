import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCarrito } from '../../context/CarritoContext';
import { aCentavos, formatoMoneda } from '../../utils/dinero';

export default function Destacados({ productos, error }) {
  const { usuario } = useAuth();
  const { agregar } = useCarrito();
  const navigate = useNavigate();

  // Agregar al carrito requiere sesión: sin ella, se pide login y se vuelve a la tienda
  const comprar = (producto) => {
    if (!usuario) {
      navigate('/login', { state: { from: '/productos' } });
      return;
    }
    agregar(producto);
  };

  return (
    <section className="seccion" id="destacados" aria-labelledby="titulo-destacados">
      <h2 id="titulo-destacados">Productos destacados</h2>
      {error && <p className="order-message error">{error}</p>}
      {!error && !productos.length && <p className="empty-cart">Cargando productos...</p>}

      <div className="destacados-grid">
        {productos.map((p) => (
          <article className="product-card" key={p.id}>
            <div className="product-image">
              {p.imagen && <img src={p.imagen} alt={p.nombre} loading="lazy" />}
            </div>
            <h3>{p.nombre}</h3>
            <small className="categoria-label">{p.categoria?.nombre}</small>
            <div className="product-footer">
              <strong>{formatoMoneda(aCentavos(p.precio))}</strong>
              <button
                className="add-button"
                onClick={() => comprar(p)}
                disabled={p.stock < 1}
                aria-label={`Agregar ${p.nombre} al carrito`}
                title={usuario ? 'Agregar al carrito' : 'Inicia sesión para comprar'}
              >+</button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
