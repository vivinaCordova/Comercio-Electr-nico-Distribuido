import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { aCentavos, formatoMoneda } from '../../utils/dinero';
import imagenProducto from '../../assets/imagen-producto.svg';
import { useAuth } from '../../context/AuthContext';

export default function TarjetaProducto({ producto, onAgregar, mostrarAgregar = true }) {
  const { id, nombre, precio, stock, categoria, imagen } = producto;
  const { usuario } = useAuth();
  const navigate = useNavigate();
  const agregar = () => {
    if (!usuario) {
      navigate('/login', { state: { from: `/productos/${id}` } });
      return;
    }
    onAgregar?.(producto);
  };
  return (
    <article className="product-card">
      <Link className="product-card-link" to={`/productos/${id}`} aria-label={`Ver ${nombre}`}>
        <div className="product-image">
          <img
            src={imagen || imagenProducto}
            alt={nombre || 'Producto'}
            loading="lazy"
            onError={(event) => { event.currentTarget.src = imagenProducto; }}
          />
        </div>
        <h2>{nombre}</h2>
      </Link>
      <small className="categoria-label">{categoria?.nombre || 'Producto'}</small>
      {stock < 1 && <small className="stock-card agotado">Sin stock</small>}
      {stock > 0 && stock <= 5 && <small className="stock-card">Quedan {stock}</small>}
      <div className="product-footer">
        <strong>{formatoMoneda(aCentavos(precio))}</strong>
        {mostrarAgregar && (
          <button
            className="add-button"
            onClick={agregar}
            disabled={stock < 1 || !onAgregar}
            aria-label={`Agregar ${nombre} al carrito`}
            title={stock < 1 ? 'Agotado' : 'Agregar al carrito'}
          >Agregar</button>
        )}
      </div>
    </article>
  );
}
