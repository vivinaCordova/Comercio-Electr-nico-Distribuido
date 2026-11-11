import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/layout/MainLayout.jsx';
import RutaProtegida from './components/layout/RutaProtegida.jsx';
import AuthPage from './pages/AuthPage.jsx';
import HomePage from './pages/HomePage.jsx';
import CheckoutPage from './pages/CheckoutPage.jsx';
import OrdersPage from './pages/OrdersPage.jsx';

export default function App() {
  return (
    <Routes>
      <Route path="login" element={<AuthPage modo="login" />} />
      <Route path="registro" element={<AuthPage modo="registro" />} />
      <Route element={<RutaProtegida><MainLayout /></RutaProtegida>}>
        <Route index element={<Navigate to="/productos" replace />} />
        <Route path="productos" element={<HomePage />} />
        <Route path="carrito" element={<HomePage />} />
        <Route path="checkout" element={<CheckoutPage />} />
        <Route path="ordenes" element={<OrdersPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
