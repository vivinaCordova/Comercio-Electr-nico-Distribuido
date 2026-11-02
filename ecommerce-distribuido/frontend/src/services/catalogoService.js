import { api } from './api';
export const catalogoService = { estado: () => api.get('/api/catalogo/health') };
