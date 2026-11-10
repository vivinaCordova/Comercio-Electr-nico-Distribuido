import React, { useEffect, useState } from 'react';
import LandingHeader from '../components/landing/LandingHeader.jsx';
import Hero from '../components/landing/Hero.jsx';
import Beneficios from '../components/landing/Beneficios.jsx';
import CategoriasGrid from '../components/landing/CategoriasGrid.jsx';
import Destacados from '../components/landing/Destacados.jsx';
import PieLanding from '../components/landing/PieLanding.jsx';
import { catalogoService } from '../services/catalogoService';
import { textoError } from '../utils/errores';

// Destacados con variedad: toma un producto por categoría en turnos (round-robin)
const destacadosPorCategoria = (lista, cantidad) => {
  const grupos = Object.values(lista.reduce((mapa, p) => {
    const clave = p.categoria?.slug || 'otros';
    (mapa[clave] ||= []).push(p);
    return mapa;
  }, {}));
  const elegidos = [];
  for (let turno = 0; elegidos.length < cantidad && grupos.some((g) => g[turno]); turno++) {
    grupos.forEach((g) => { if (g[turno] && elegidos.length < cantidad) elegidos.push(g[turno]); });
  }
  return elegidos;
};

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
        <Destacados productos={destacadosPorCategoria(productos, 8)} error={error} />
      </main>
      <PieLanding />
    </div>
  );
}
