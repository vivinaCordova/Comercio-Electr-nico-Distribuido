import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
const links = [['/', 'Inicio'], ['/productos','Productos'], ['/carrito','Carrito'], ['/checkout','Checkout'], ['/ordenes','Ã“rdenes']];
export default function MainLayout(){return <><header><strong>E-commerce distribuido</strong><nav>{links.map(([href,label])=><NavLink key={href} to={href} end={href==='/'}>{label}</NavLink>)}</nav></header><main><Outlet/></main><footer>Base acadÃ©mica Â· Microservicios Â· Funcionalidades en desarrollo</footer></>}
