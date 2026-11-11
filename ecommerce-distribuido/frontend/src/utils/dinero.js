// Promoción vigente: 12% de descuento sobre el subtotal (el servidor la vuelve a calcular)
export const PROMO_SELL12 = 'SELL12';

// Todo se calcula en centavos (enteros) para que coincida con el redondeo de ms-ordenes
export const aCentavos = (valor) => Math.round(Number(valor) * 100);

export const formatoMoneda = (centavos) =>
  `$${(centavos / 100).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

export function calcularTotales(items, promo) {
  const subtotal = items.reduce((suma, item) => suma + aCentavos(item.precio) * item.cantidad, 0);
  const descuento = promo === PROMO_SELL12 ? Math.round((subtotal * 12) / 100) : 0;
  return { subtotal, descuento, total: subtotal - descuento };
}
