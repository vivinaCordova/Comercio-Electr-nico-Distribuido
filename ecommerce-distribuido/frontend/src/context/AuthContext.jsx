import React from 'react';
import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);
const KEY = 'sellpilot_user';

const leer = () => { try { return JSON.parse(localStorage.getItem(KEY)); } catch { return null; } };

// Patrón Provider: expone la sesión a toda la app
export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(leer);
  const iniciar = (u) => { setUsuario(u); try { localStorage.setItem(KEY, JSON.stringify(u)); } catch { /* sin storage */ } };
  const salir = () => { setUsuario(null); try { localStorage.removeItem(KEY); } catch { /* sin storage */ } };
  return <AuthContext.Provider value={{ usuario, iniciar, salir }}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
