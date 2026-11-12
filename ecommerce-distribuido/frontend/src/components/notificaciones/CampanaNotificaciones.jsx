import React, { useCallback, useEffect, useId, useRef, useState } from 'react';
import { notificacionesService } from '../../services/notificaciones';
import { textoError } from '../../utils/errores';
import './notificaciones.css';

function Icono() {
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true"><path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9" /><path d="M10 21h4" /></svg>;
}

function tiempoRelativo(fecha) {
  const tiempo = new Date(fecha).getTime();
  if (!Number.isFinite(tiempo)) return 'Fecha no disponible';
  const minutos = Math.max(0, Math.floor((Date.now() - tiempo) / 60000));
  if (minutos < 1) return 'Ahora';
  if (minutos < 60) return `Hace ${minutos} min`;
  const horas = Math.floor(minutos / 60);
  if (horas < 24) return `Hace ${horas} h`;
  return new Intl.DateTimeFormat('es', { day: 'numeric', month: 'short' }).format(tiempo);
}

export default function CampanaNotificaciones({ usuarioId }) {
  const id = useId();
  const contenedor = useRef(null);
  const disparador = useRef(null);
  const titulo = useRef(null);
  const secuencia = useRef(0);
  const ocupado = useRef(false);
  const montado = useRef(false);
  const [abierto, setAbierto] = useState(false);
  const [lista, setLista] = useState([]);
  const [noLeidas, setNoLeidas] = useState(0);
  const [cargando, setCargando] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState('');
  const [aviso, setAviso] = useState('');

  const cargar = useCallback(async () => {
    if (ocupado.current) return;
    const solicitud = ++secuencia.current;
    setCargando(true);
    try {
      const [notificaciones, cantidad] = await Promise.all([
        notificacionesService.listar(usuarioId),
        notificacionesService.contarNoLeidas(usuarioId),
      ]);
      if (!montado.current || solicitud !== secuencia.current) return;
      setLista(notificaciones);
      setNoLeidas(cantidad);
      setError('');
    } catch (e) {
      if (montado.current && solicitud === secuencia.current) {
        setError(textoError(e, 'No pudimos actualizar tus notificaciones. Intenta de nuevo.'));
      }
    } finally {
      if (montado.current && solicitud === secuencia.current) setCargando(false);
    }
  }, [usuarioId]);

  useEffect(() => {
    montado.current = true;
    cargar();
    const intervalo = window.setInterval(() => {
      if (!document.hidden) cargar();
    }, 10000);
    return () => {
      montado.current = false;
      secuencia.current += 1;
      window.clearInterval(intervalo);
    };
  }, [cargar]);

  const cerrar = useCallback(() => {
    setAbierto(false);
    disparador.current?.focus();
  }, []);

  useEffect(() => {
    if (!abierto) return;
    titulo.current?.focus();
    const fuera = (e) => {
      if (!contenedor.current?.contains(e.target)) setAbierto(false);
    };
    const teclado = (e) => { if (e.key === 'Escape') cerrar(); };
    document.addEventListener('pointerdown', fuera);
    document.addEventListener('keydown', teclado);
    return () => {
      document.removeEventListener('pointerdown', fuera);
      document.removeEventListener('keydown', teclado);
    };
  }, [abierto, cerrar]);

  const marcar = async (notificacion) => {
    if (ocupado.current) return;
    ocupado.current = true;
    secuencia.current += 1;
    setCargando(false);
    setGuardando(true);
    setAviso('');
    try {
      if (notificacion) await notificacionesService.marcarLeida(notificacion.id);
      else await notificacionesService.marcarTodas(usuarioId);
      if (!montado.current) return;
      setAviso(notificacion ? 'Notificación marcada como leída.' : 'Todas tus notificaciones están al día.');
    } catch (e) {
      if (montado.current) setError(textoError(e, 'No pudimos marcar como leída. Intenta de nuevo.'));
      return;
    } finally {
      ocupado.current = false;
      if (montado.current) setGuardando(false);
    }
    // Consultar otra vez conserva el contador real, incluso si llegaron nuevos eventos.
    if (montado.current) await cargar();
  };

  return <div className="avisos" ref={contenedor} onBlur={(e) => {
    if (e.relatedTarget && !e.currentTarget.contains(e.relatedTarget)) setAbierto(false);
  }}>
    <button type="button" className="avisos-disparador" ref={disparador}
      aria-label={`Notificaciones${noLeidas ? `: ${noLeidas} sin leer` : ''}`}
      aria-expanded={abierto} aria-controls={`${id}-panel`} aria-haspopup="dialog"
      onClick={() => { setAbierto(!abierto); if (!abierto) cargar(); }}>
      <Icono />
      {noLeidas > 0 && <span className="avisos-contador" aria-hidden="true">{noLeidas > 99 ? '99+' : noLeidas}</span>}
    </button>
    {abierto && <section className="avisos-panel" id={`${id}-panel`} role="dialog" aria-labelledby={`${id}-titulo`}>
      <header className="avisos-cabecera">
        <h2 id={`${id}-titulo`} ref={titulo} tabIndex={-1}>Notificaciones</h2>
        <button type="button" onClick={() => marcar()} disabled={!noLeidas || guardando || cargando}>{guardando ? 'Guardando…' : 'Marcar todas como leídas'}</button>
      </header>
      {error && <div className="avisos-error" role="alert"><p>{error}</p><button type="button" onClick={cargar} disabled={cargando || guardando}>Reintentar</button></div>}
      <div className="avisos-contenido" aria-busy={cargando || guardando}>
        {cargando && lista.length === 0 ? <p className="avisos-vacio" role="status">Cargando notificaciones…</p>
          : lista.length === 0 && !error ? <div className="avisos-vacio"><strong>No tienes notificaciones</strong>
            <p>Aquí verás las novedades de tus pedidos.</p></div>
            : <ul className="avisos-lista">{lista.map((n) => {
              const fecha = new Date(n.fechaCreacion);
              const contenido = <><strong>{n.titulo}</strong><span className="aviso-mensaje">{n.mensaje}</span>
                <time dateTime={Number.isFinite(fecha.getTime()) ? fecha.toISOString() : undefined} title={Number.isFinite(fecha.getTime()) ? fecha.toLocaleString('es') : undefined}>{tiempoRelativo(n.fechaCreacion)}</time>
                {!n.leida && <span className="aviso-punto" aria-hidden="true" />}</>;
              return <li key={n.id} className={`aviso-item ${n.leida ? '' : 'aviso-sin-leer'}`}>
                {n.leida ? <div className="aviso-texto">{contenido}</div>
                  : <button type="button" className="aviso-texto" onClick={() => marcar(n)} disabled={guardando || cargando} aria-label={`Marcar como leída: ${n.titulo}`}>{contenido}</button>}
              </li>;
            })}</ul>}
      </div>
      <p className="avisos-anuncio sr-only" role="status">{aviso}</p>
    </section>}
  </div>;
}
