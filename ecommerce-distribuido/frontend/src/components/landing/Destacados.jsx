import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCarrito } from '../../context/CarritoContext';
import TarjetaProducto from '../products/TarjetaProducto.jsx';

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
          <TarjetaProducto key={p.id} producto={p} onAgregar={comprar} />
        ))}
      </div>
    </section>
  );
}
