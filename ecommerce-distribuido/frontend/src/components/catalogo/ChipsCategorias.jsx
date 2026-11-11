import React from 'react';
import IconoCategoria from './IconoCategoria.jsx';

// Filtro por categoría: "Todas" (slug vacío) más una opción por categoría con su conteo
export default function ChipsCategorias({ categorias, activa, total, onElegir }) {
  const opciones = [{ slug: '', nombre: 'Todas', icono: 'todas', cantidadProductos: total }, ...categorias];

  return (
    <nav className="chips" aria-label="Filtrar por categoría">
      {opciones.map((c) => (
        <button
          key={c.slug || 'todas'}
          type="button"
          className={`chip${activa === c.slug ? ' activo' : ''}`}
          aria-pressed={activa === c.slug}
          onClick={() => onElegir(c.slug)}
        >
          <IconoCategoria clave={c.icono} className="chip-icono" /> {c.nombre} <em>{c.cantidadProductos}</em>
        </button>
      ))}
    </nav>
  );
}
