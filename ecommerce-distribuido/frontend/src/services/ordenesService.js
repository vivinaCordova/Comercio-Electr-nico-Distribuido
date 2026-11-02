import { api } from './api';
export const ordenesService = { estado: () => api.get('/api/ordenes/health') };
