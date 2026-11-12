import React, { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import Marca from './Marca.jsx';
import { useAuth } from '../../context/AuthContext';
import { useCarrito } from '../../context/CarritoContext';
import CampanaNotificaciones from '../notificaciones/CampanaNotificaciones.jsx';

const ENLACES = [['/productos', '◈', 'Productos'], ['/ordenes', '♧', 'Mis órdenes'], ['/carrito', '🛒', 'Carrito']];

export default function Encabezado() {
  const { usuario, salir } = useAuth();
  const { cantidadTotal } = useCarrito();
  const navigate = useNavigate();
  const [busqueda, setBusqueda] = useState('');
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
      <form className="header-search" onSubmit={(e) => { e.preventDefault(); navigate(`/productos?q=${encodeURIComponent(busqueda)}`); }}>
        <input value={busqueda} onChange={(e) => setBusqueda(e.target.value)} placeholder="Buscar" aria-label="Buscar productos" />
        <button type="submit" aria-label="Buscar">⌕</button>
      </form>
      <div className="profile-actions">
        {usuario?.id && <CampanaNotificaciones key={usuario.id} usuarioId={usuario.id} />}
        <span className="avatar" title={usuario?.email}>{(usuario?.nombre || '?')[0].toUpperCase()}</span>
        <div className="profile-copy"><strong>{usuario?.nombre}</strong><small>{usuario?.email}</small></div>
        <button className="salir" onClick={cerrar}>Salir</button>
      </div>
    </header>
  );
}
