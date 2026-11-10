import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import Marca from './Marca.jsx';
import { useAuth } from '../../context/AuthContext';
import { useCarrito } from '../../context/CarritoContext';

const ENLACES = [['/productos', '◈', 'Productos'], ['/ordenes', '♧', 'Mis órdenes'], ['/carrito', '🛒', 'Carrito']];

export default function Encabezado() {
  const { usuario, salir } = useAuth();
  const { cantidadTotal } = useCarrito();
  const navigate = useNavigate();
  const cerrar = () => { salir(); navigate('/login'); };

  return (
    <header className="shop-header">
      <Marca />
      <nav className="shop-nav">
        {ENLACES.map(([ruta, icono, texto]) => (
          <NavLink key={ruta} to={ruta}>
            {icono} <span>{texto}</span>
            {ruta === '/carrito' && cantidadTotal > 0 && <em className="badge">{cantidadTotal}</em>}
          </NavLink>
        ))}
      </nav>
      <div className="profile-actions">
        <span className="avatar" title={usuario?.email}>{(usuario?.nombre || '?')[0].toUpperCase()}</span>
        <button className="salir" onClick={cerrar}>Salir</button>
      </div>
    </header>
  );
}
