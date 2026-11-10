import { api } from './api';

// Contrato con ms-usuarios (a través del Gateway)
export const authService = {
  registro: (datos) => api.post('/api/usuarios/registro', datos).then((r) => r.data),
  login: (datos) => api.post('/api/usuarios/login', datos).then((r) => r.data),
};
