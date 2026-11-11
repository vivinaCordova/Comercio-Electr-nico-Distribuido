import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import IconoCategoria from '../catalogo/IconoCategoria.jsx';

const etiquetaProductos = (n) => `${n} ${n === 1 ? 'producto' : 'productos'}`;

// Sin sesión, la categoría pide login y, después de entrar, lleva a ella
export default function CategoriasGrid({ categorias }) {
  const { usuario } = useAuth();

  return (
    <section className="seccion" id="categorias" aria-labelledby="titulo-categorias">
      <h2 id="titulo-categorias">Explora por categoría</h2>
      <div className="categorias-grid">
        {categorias.map((c) => {
          const ruta = `/productos?categoria=${c.slug}`;
          return (
            <Link key={c.slug} className="categoria-tile" to={usuario ? ruta : '/login'} state={usuario ? undefined : { from: ruta }}>
              <IconoCategoria clave={c.icono} className="categoria-icono" />
              <strong>{c.nombre}</strong>
              <small>{c.descripcion}</small>
              <em>{etiquetaProductos(c.cantidadProductos)}</em>
            </Link>
          );
        })}
      </div>
    </section>
  );
}
