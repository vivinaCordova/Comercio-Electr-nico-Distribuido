import React from 'react';
import IconoCategoria from '../catalogo/IconoCategoria.jsx';

// Solo describe lo que el sistema hace de verdad
const BENEFICIOS = [
  ['envio', 'Pedidos en línea', 'Pide desde la tienda; el sistema confirma tu orden según el stock disponible.'],
  ['pago', 'Compra segura', 'Compras con tu cuenta registrada; el total, con la promoción, lo calcula el servidor.'],
  ['soporte', 'Seguimiento', 'Revisa el estado de tus pedidos y el motivo de cancelación en «Mis órdenes».'],
];

export default function Beneficios() {
  return (
    <section className="beneficios" aria-label="Beneficios">
      {BENEFICIOS.map(([clave, titulo, texto]) => (
        <article className="beneficio" key={titulo}>
          <IconoCategoria clave={clave} className="beneficio-icono" />
          <div>
            <h3>{titulo}</h3>
            <p>{texto}</p>
          </div>
        </article>
      ))}
    </section>
  );
}
