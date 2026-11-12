import { api } from './api';

const ruta = '/api/notificaciones';

export const notificacionesService = {
  listar: (usuarioId, soloNoLeidas = false) => api.get(ruta, { params: { userId: usuarioId, soloNoLeidas } }).then((r) => r.data),
  contarNoLeidas: (usuarioId) => api.get(`${ruta}/no-leidas/count`, { params: { userId: usuarioId } }).then((r) => r.data.count),
  marcarLeida: (id) => api.patch(`${ruta}/${id}/leida`),
  marcarTodas: (usuarioId) => api.post(`${ruta}/leer-todas`, null, { params: { userId: usuarioId } }),
};
