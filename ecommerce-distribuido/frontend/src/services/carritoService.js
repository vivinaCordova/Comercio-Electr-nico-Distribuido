import { api } from './api';
export const carritoService = { estado: () => api.get('/api/carrito/health') };
