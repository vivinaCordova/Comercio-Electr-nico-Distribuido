import React from 'react';

// Cambia el nombre de la marca en UN solo lugar
export const NOMBRE_MARCA = ['E-', 'commerce'];

export default function Marca() {
  return <a className="brand" href="/">● <span>{NOMBRE_MARCA[0]}</span>{NOMBRE_MARCA[1]}</a>;
}
