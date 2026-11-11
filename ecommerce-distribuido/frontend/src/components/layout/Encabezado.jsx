import React, { useEffect, useRef, useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import Marca from './Marca.jsx';
import { useAuth } from '../../context/AuthContext';
import { useCarrito } from '../../context/CarritoContext';
import { notificacionesService } from '../../services/notificaciones';
import { textoError } from '../../utils/errores';

const ENLACES = [['/productos', '◈', 'Productos'], ['/ordenes', '♧', 'Mis órdenes'], ['/carrito', '🛒', 'Carrito']];

export default function Encabezado() {
  const { usuario, salir } = useAuth();
  const { cantidadTotal } = useCarrito();
  const navigate = useNavigate();
  const [busqueda, setBusqueda] = useState('');
  const [notificaciones, setNotificaciones] = useState([]);
  const [noLeidas, setNoLeidas] = useState(0);
  const [abierto, setAbierto] = useState(false);
  const [errorNotificaciones, setErrorNotificaciones] = useState('');
  const notificacionesRef = useRef(null);
  const cerrar = () => { salir(); navigate('/login'); };

  const cargarNotificaciones = async () => {
    if (!usuario?.id) return;
    try {
      const [lista, cantidad] = await Promise.all([
        notificacionesService.listar(usuario.id),
        notificacionesService.contarNoLeidas(usuario.id),
      ]);
      setNotificaciones(lista);
      setNoLeidas(cantidad);
      setErrorNotificaciones('');
    } catch (error) {
      setNoLeidas(0);
      setErrorNotificaciones(textoError(error, 'No se pudo cargar'));
    }
  };

  useEffect(() => {
    cargarNotificaciones();
    const intervalo = window.setInterval(cargarNotificaciones, 10000);
    return () => window.clearInterval(intervalo);
  }, [usuario?.id]);

  useEffect(() => {
    const cerrarFuera = (event) => {
      if (notificacionesRef.current && !notificacionesRef.current.contains(event.target)) setAbierto(false);
    };
    document.addEventListener('mousedown', cerrarFuera);
    return () => document.removeEventListener('mousedown', cerrarFuera);
  }, []);

  const marcarTodas = async () => {
    try {
      await notificacionesService.marcarTodas(usuario.id);
      setNotificaciones((actuales) => actuales.map((n) => ({ ...n, leida: true })));
      setNoLeidas(0);
    } catch (error) {
      setErrorNotificaciones(textoError(error, 'No se pudo cargar'));
    }
  };

  const marcarUna = async (notificacion) => {
    if (notificacion.leida) return;
    try {
      await notificacionesService.marcarLeida(notificacion.id);
      setNotificaciones((actuales) => actuales.map((n) => n.id === notificacion.id ? { ...n, leida: true } : n));
      setNoLeidas((cantidad) => Math.max(0, cantidad - 1));
    } catch (error) {
      setErrorNotificaciones(textoError(error, 'No se pudo cargar'));
    }
  };

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
        <div className="notification-wrapper" ref={notificacionesRef}>
          <button className="notification-button" onClick={() => { setAbierto(!abierto); if (!abierto) cargarNotificaciones(); }} aria-label="Notificaciones">
            ♧{noLeidas > 0 && <span className="notification-count">{noLeidas > 99 ? '99+' : noLeidas}</span>}
          </button>
          {abierto && <div className="notification-panel">
            <div className="notification-heading">
              <strong>Notificaciones</strong>
              <button onClick={marcarTodas} disabled={!noLeidas}>Marcar todas como leídas</button>
            </div>
            {errorNotificaciones ? <p className="notification-empty">{errorNotificaciones}</p> : notificaciones.length === 0 ? <p className="notification-empty">No tienes notificaciones</p> : (
              <ul>
                {notificaciones.slice(0, 10).map((notificacion) => (
                  <li key={notificacion.id} className={notificacion.leida ? '' : 'unread'} onClick={() => marcarUna(notificacion)}>
                    <strong>{notificacion.titulo}</strong>
                    <span>{notificacion.mensaje}</span>
                    <small>{tiempoRelativo(notificacion.fechaCreacion)}</small>
                  </li>
                ))}
              </ul>
            )}
          </div>}
        </div>
        <span className="avatar" title={usuario?.email}>{(usuario?.nombre || '?')[0].toUpperCase()}</span>
        <div className="profile-copy"><strong>{usuario?.nombre}</strong><small>{usuario?.email}</small></div>
        <button className="salir" onClick={cerrar}>Salir</button>
      </div>
    </header>
  );
}

function tiempoRelativo(fecha) {
  const minutos = Math.max(0, Math.floor((Date.now() - new Date(fecha).getTime()) / 60000));
  if (minutos < 1) return 'Ahora';
  if (minutos < 60) return `Hace ${minutos} min`;
  const horas = Math.floor(minutos / 60);
  if (horas < 24) return `Hace ${horas} h`;
  return `Hace ${Math.floor(horas / 24)} d`;
}
