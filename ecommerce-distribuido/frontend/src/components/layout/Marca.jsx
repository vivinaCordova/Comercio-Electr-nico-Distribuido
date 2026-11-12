import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

// Cambia el nombre de la marca en UN solo lugar
export const NOMBRE_MARCA = ['E-', 'commerce'];

// Con sesión, el logo lleva a la tienda; sin sesión, a la landing pública
export default function Marca() {
  const { usuario } = useAuth();
  return <Link className="brand" to={usuario ? '/productos' : '/'}>● <span>{NOMBRE_MARCA[0]}</span>{NOMBRE_MARCA[1]}</Link>;
}
