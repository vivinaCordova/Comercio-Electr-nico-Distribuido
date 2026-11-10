import { api } from './api';
export const ordenesService = {
  estado: () => api.get('/api/ordenes/health'),
  crear: (orden) => api.post('/ordenes', orden).then((r) => r.data),
  listar: (usuarioId) => api.get('/ordenes', { params: { userId: usuarioId } }).then((r) => r.data),
};
