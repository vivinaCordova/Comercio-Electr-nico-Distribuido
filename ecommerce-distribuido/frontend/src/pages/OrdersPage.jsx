import React from 'react';
import Encabezado from '../components/layout/Encabezado.jsx';

export default function OrdersPage() {
  return (
    <div className="shop-shell">
      <Encabezado />
      <div className="shop-content">
        <section className="products-column">
          <div className="section-heading"><h1>Mis órdenes</h1></div>
          <p>Aquí aparecerá el historial de tus pedidos.</p>
        </section>
      </div>
    </div>
  );
}
