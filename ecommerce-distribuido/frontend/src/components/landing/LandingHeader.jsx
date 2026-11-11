import React from 'react';
import { Link } from 'react-router-dom';
import Marca from '../layout/Marca.jsx';
import { useAuth } from '../../context/AuthContext';

export default function LandingHeader() {
  const { usuario } = useAuth();

  return (
    <header className="landing-header">
      <Marca />
      <nav className="landing-nav" aria-label="Secciones de la página">
        <a href="#categorias">Categorías</a>
        <a href="#destacados">Destacados</a>
      </nav>
      <div className="landing-acciones">
        {usuario ? (
          <Link className="cta" to="/productos">Ir a la tienda</Link>
        ) : (
          <>
            <Link className="cta-secundario" to="/login">Iniciar sesión</Link>
            <Link className="cta" to="/registro">Crear cuenta</Link>
          </>
        )}
      </div>
    </header>
  );
}
