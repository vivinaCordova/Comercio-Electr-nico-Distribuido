import { api } from './api';

const ruta = (usuarioId) => `/api/carrito/${usuarioId}`;

export const carritoService = {
  estado: () => api.get('/api/carrito/health'),
  obtener: (usuarioId) => api.get(ruta(usuarioId)).then((r) => r.data),
  agregar: (usuarioId, productoId, cantidad = 1, variantes = {}) => api.post(`${ruta(usuarioId)}/items`, { productoId, cantidad, variantes }).then((r) => r.data),
  cambiar: (usuarioId, productoId, cantidad) => api.put(`${ruta(usuarioId)}/items/${productoId}`, { productoId, cantidad }).then((r) => r.data),
  quitar: (usuarioId, productoId) => api.delete(`${ruta(usuarioId)}/items/${productoId}`).then((r) => r.data),
  vaciar: (usuarioId) => api.delete(ruta(usuarioId)),
};
