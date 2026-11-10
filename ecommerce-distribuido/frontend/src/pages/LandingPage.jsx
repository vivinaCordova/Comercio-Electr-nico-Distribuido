import React, { useEffect, useState } from 'react';
import LandingHeader from '../components/landing/LandingHeader.jsx';
import Hero from '../components/landing/Hero.jsx';
import Beneficios from '../components/landing/Beneficios.jsx';
import CategoriasGrid from '../components/landing/CategoriasGrid.jsx';
import Destacados from '../components/landing/Destacados.jsx';
import PieLanding from '../components/landing/PieLanding.jsx';
import { catalogoService } from '../services/catalogoService';
import { textoError } from '../utils/errores';

// Página pública: ver productos y categorías no requiere sesión
export default function LandingPage() {
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    Promise.all([catalogoService.listar(), catalogoService.categorias()])
      .then(([lista, cats]) => { setProductos(lista); setCategorias(cats); })
      .catch((e) => setError(textoError(e, 'No pudimos cargar el catálogo. Intenta más tarde.')));
  }, []);

  return (
    <div className="landing">
      <LandingHeader />
      <main>
        <Hero productos={productos} />
        <Beneficios />
        <CategoriasGrid categorias={categorias} />
        <Destacados productos={productos.slice(0, 8)} error={error} />
      </main>
      <PieLanding />
    </div>
  );
}
