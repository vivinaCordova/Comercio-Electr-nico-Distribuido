import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

// Sin sesión, redirige a /login recordando la ruta para volver a ella tras entrar
export default function RutaProtegida({ children }) {
  const { usuario } = useAuth();
  const location = useLocation();
  return usuario ? children : <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />;
}
