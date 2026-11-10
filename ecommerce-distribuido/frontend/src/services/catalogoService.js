import { api } from './api';

const BASE = '/api/catalogo/productos';

export const catalogoService = {
  estado: () => api.get('/api/catalogo/health'),
  // Productos activos, cada uno con la URL de su imagen principal
  listar: async () => {
    const { data } = await api.get(`${BASE}/activos`, { params: { tamanio: 100 } });
    return Promise.all(data.contenido.map(async (p) => {
      const imagenes = await api.get(`${BASE}/${p.id}/imagenes`).then((r) => r.data).catch(() => []);
      const principal = imagenes.find((i) => i.esPrincipal) || imagenes[0];
      return { ...p, imagen: principal ? `${api.defaults.baseURL}${principal.url}` : null };
    }));
  },
};
