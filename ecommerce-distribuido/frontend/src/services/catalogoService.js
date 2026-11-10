import { api } from './api';

const BASE = '/api/catalogo/productos';

// Añade la URL de la imagen principal del producto
const conImagen = async (p) => {
  const imagenes = await api.get(`${BASE}/${p.id}/imagenes`).then((r) => r.data).catch(() => []);
  const principal = imagenes.find((i) => i.esPrincipal) || imagenes[0];
  return { ...p, imagen: principal ? `${api.defaults.baseURL}${principal.url}` : null };
};

export const catalogoService = {
  estado: () => api.get('/api/catalogo/health'),
  // Productos activos con imagen. Filtros opcionales: categoria (slug) y q (texto en el nombre)
  listar: async ({ categoria, q } = {}) => {
    const { data } = await api.get(`${BASE}/activos`, { params: { tamanio: 100, categoria: categoria || undefined, q: q || undefined } });
    return Promise.all(data.contenido.map(conImagen));
  },
  obtener: (id) => api.get(`${BASE}/${id}`).then((r) => conImagen(r.data)),
  // Categorías oficiales con la cantidad de productos activos
  categorias: () => api.get('/api/catalogo/categorias').then((r) => r.data),
};
