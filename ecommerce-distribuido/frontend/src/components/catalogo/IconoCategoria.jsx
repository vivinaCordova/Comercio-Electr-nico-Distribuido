import React from 'react';

// Iconos SVG en línea (trazo con currentColor). La clave coincide con categoria.icono en el backend
const RUTAS = {
  laptop: <><rect x="4" y="5" width="16" height="11" rx="2" /><path d="M2 20h20" /></>,
  camiseta: <path d="M8 3 3 6l2 4 3-1v12h8V9l3 1 2-4-5-3c-1 1.5-2.5 2.5-4 2.5S9 4 8 3z" />,
  hogar: <><path d="M3 11l9-7 9 7" /><path d="M5 10v10h14V10" /><path d="M10 20v-5h4v5" /></>,
  alimentos: <><path d="M5 19c0-8 5-13 14-14 0 9-5 14-13 14z" /><path d="M5 19l7-7" /></>,
  belleza: <><path d="M12 3l1.8 4.7L18.5 9.5l-4.7 1.8L12 16l-1.8-4.7L5.5 9.5l4.7-1.8z" /><path d="M19 16l.8 2 2 .8-2 .8-.8 2-.8-2-2-.8 2-.8z" /></>,
  deportes: <><circle cx="12" cy="12" r="9" /><path d="M12 7.5l3.4 2.5-1.3 4h-4.2l-1.3-4z" /></>,
  libros: <><path d="M12 6.5C10.5 5 8 4.5 4 4.5v14c4 0 6.5.5 8 2 1.5-1.5 4-2 8-2v-14c-4 0-6.5.5-8 2z" /><path d="M12 6.5v14" /></>,
  videojuegos: <><path d="M7 8h10a5 5 0 0 1 4.8 6.4l-.8 2.5a2 2 0 0 1-3.6.4L15 15H9l-1.4 2.3a2 2 0 0 1-3.6-.4l-.8-2.5A5 5 0 0 1 7 8z" /><path d="M7.5 11v3M6 12.5h3" /></>,
  mascotas: <><ellipse cx="12" cy="16" rx="4" ry="3.5" /><circle cx="6" cy="10.5" r="1.8" /><circle cx="9.5" cy="6.5" r="1.8" /><circle cx="14.5" cy="6.5" r="1.8" /><circle cx="18" cy="10.5" r="1.8" /></>,
  automotriz: <><path d="M3 13l2-5h14l2 5v4H3z" /><circle cx="7.5" cy="17.5" r="1.5" /><circle cx="16.5" cy="17.5" r="1.5" /></>,
  todas: <><rect x="4" y="4" width="6" height="6" rx="1" /><rect x="14" y="4" width="6" height="6" rx="1" /><rect x="4" y="14" width="6" height="6" rx="1" /><rect x="14" y="14" width="6" height="6" rx="1" /></>,
  envio: <><path d="M2 6h12v10H2z" /><path d="M14 10h4l3 3v3h-7" /><circle cx="6" cy="17.5" r="1.8" /><circle cx="17" cy="17.5" r="1.8" /></>,
  pago: <><rect x="3" y="10" width="18" height="11" rx="2" /><path d="M7 10V7a5 5 0 0 1 10 0v3" /></>,
  soporte: <path d="M4 5h16v11H9l-5 4V5z" />,
};

export default function IconoCategoria({ clave, className = 'icono' }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"
      strokeLinecap="round" strokeLinejoin="round" aria-hidden="true" focusable="false">
      {RUTAS[clave] || RUTAS.todas}
    </svg>
  );
}
