import React from 'react';
import { createContext, useContext, useEffect, useState } from 'react';
import { useAuth } from './AuthContext';
import { carritoService } from '../services/carritoService';
import { catalogoService } from '../services/catalogoService';
import { PROMO_SELL12, calcularTotales } from '../utils/dinero';
import { textoError } from '../utils/errores';

const CarritoContext = createContext(null);

// Patrón Provider: une el carrito (ms-carrito) con los datos de producto (ms-catalogo).
// Productos, Carrito y Checkout leen de aquí, así muestran y cobran exactamente lo mismo.
export function CarritoProvider({ children }) {
  const { usuario } = useAuth();
  const [lineas, setLineas] = useState([]);       // [{ productoId, cantidad }] tal como lo guarda ms-carrito
  const [productos, setProductos] = useState({}); // id -> producto con imagen
  const [promo, setPromo] = useState('');
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!usuario) {
      setLineas([]);
      setProductos({});
      return undefined;
    }
    let activo = true;
    setCargando(true);
    carritoService.obtener(usuario.id)
      .then((carrito) => activo && recibirCarrito(carrito))
      .catch((e) => activo && setError(textoError(e, 'No se pudo cargar el carrito. Verifica ms-carrito y el Gateway.')))
      .finally(() => { if (activo) setCargando(false); });
    return () => { activo = false; };
  }, [usuario?.id]);

  // Descarga los productos que aún no conocemos (p. ej. fuera de la primera página del catálogo)
  const recibirCarrito = async (carrito, conocidos = {}) => {
    const conocer = { ...productos, ...conocidos };
    const faltantes = carrito.items.map((l) => l.productoId).filter((id) => !conocer[id]);
    const descargados = await Promise.all(faltantes.map((id) =>
      catalogoService.obtener(id).then((p) => [id, p]).catch(() => null)));
    setProductos((prev) => ({ ...prev, ...conocidos, ...Object.fromEntries(descargados.filter(Boolean)) }));
    setLineas(carrito.items);
  };

  // Ejecuta una acción del carrito; deja el mensaje de error legible en `error`
  const ejecutar = async (accion, porDefecto) => {
    setError(null);
    try {
      await accion();
      return true;
    } catch (e) {
      setError(textoError(e, porDefecto));
      return false;
    }
  };

  const items = lineas.map((l) => {
    const producto = productos[l.productoId];
    return producto
      ? { ...producto, productoId: l.productoId, cantidad: l.cantidad, noDisponible: producto.activo === false }
      : { id: l.productoId, productoId: l.productoId, nombre: `Producto #${l.productoId}`, precio: 0, cantidad: l.cantidad, imagen: null, stock: 0, noDisponible: true };
  });
  const disponibles = items.filter((i) => !i.noDisponible);
  const totales = calcularTotales(disponibles, promo);

  const agregar = (producto, cantidad = 1, variantes = {}) => ejecutar(async () => {
    await recibirCarrito(await carritoService.agregar(usuario.id, producto.id, cantidad, variantes), { [producto.id]: producto });
  }, 'No se pudo agregar el producto al carrito.');

  const quitar = (item) => ejecutar(async () => {
    await recibirCarrito(await carritoService.quitar(usuario.id, item.productoId));
  }, 'No se pudo quitar el producto.');

  const cambiarCantidad = (item, cantidad) => (cantidad > 0
    ? ejecutar(async () => {
      await recibirCarrito(await carritoService.cambiar(usuario.id, item.productoId, cantidad));
    }, 'No se pudo actualizar la cantidad.')
    : quitar(item));

  const vaciar = () => ejecutar(async () => {
    await carritoService.vaciar(usuario.id);
    setLineas([]);
  }, 'No se pudo vaciar el carrito.');

  const aplicarPromo = (codigo) => {
    if (codigo.trim().toUpperCase() !== PROMO_SELL12) return false;
    setPromo(PROMO_SELL12);
    return true;
  };

  const value = {
    items,
    totales,
    promo,
    cargando,
    error,
    setError,
    hayNoDisponibles: items.some((i) => i.noDisponible),
    cantidadTotal: items.reduce((suma, i) => suma + i.cantidad, 0),
    agregar,
    cambiarCantidad,
    quitar,
    vaciar,
    aplicarPromo,
    quitarPromo: () => setPromo(''),
  };

  return <CarritoContext.Provider value={value}>{children}</CarritoContext.Provider>;
}

export const useCarrito = () => useContext(CarritoContext);
