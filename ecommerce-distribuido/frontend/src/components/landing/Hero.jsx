import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import imagenProducto from '../../assets/imagen-producto.svg';

// Hasta tres fotos reales del catálogo como ilustración
export default function Hero({ productos }) {
  const { usuario } = useAuth();
  const fotos = productos.filter((p) => p.imagen).slice(0, 3);

  return (
    <section className="hero">
      <div className="hero-texto">
        <span className="hero-etiqueta">Tienda en línea · UNL</span>
        <h1>Electrónica, moda, hogar y más en un solo lugar</h1>
        <p>Explora todas las categorías y compra con tu cuenta. Tu orden se confirma solo si hay stock disponible al procesarla.</p>
        <div className="hero-cta">
          {usuario ? (
            <Link className="cta" to="/productos">Ir a la tienda</Link>
          ) : (
            <>
              <Link className="cta" to="/registro">Crear cuenta</Link>
              <Link className="cta-secundario" to="/login">Iniciar sesión</Link>
            </>
          )}
        </div>
      </div>

      <div className="hero-collage">
        {fotos.map((p, i) => (
          <figure key={p.id} className={`hero-foto hero-foto-${i}`}>
            <img src={p.imagen || imagenProducto} alt={p.nombre} loading="lazy" onError={(event) => { event.currentTarget.src = imagenProducto; }} />
            <figcaption>{p.nombre}</figcaption>
          </figure>
        ))}
      </div>
    </section>
  );
}
