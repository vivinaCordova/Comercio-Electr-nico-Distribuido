import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/layout/MainLayout.jsx';
import RutaProtegida from './components/layout/RutaProtegida.jsx';
import AuthPage from './pages/AuthPage.jsx';
import LandingPage from './pages/LandingPage.jsx';
import HomePage from './pages/HomePage.jsx';
import CartPage from './pages/CartPage.jsx';
import CheckoutPage from './pages/CheckoutPage.jsx';
import OrdersPage from './pages/OrdersPage.jsx';
import ProductDetailPage from './pages/ProductDetailPage.jsx';

export default function App() {
  return (
    <Routes>
      <Route index element={<LandingPage />} />
      <Route path="login" element={<AuthPage modo="login" />} />
      <Route path="registro" element={<AuthPage modo="registro" />} />
      <Route path="productos/:id" element={<ProductDetailPage />} />
      <Route element={<RutaProtegida><MainLayout /></RutaProtegida>}>
        <Route path="productos" element={<HomePage />} />
        <Route path="carrito" element={<CartPage />} />
        <Route path="checkout" element={<CheckoutPage />} />
        <Route path="ordenes" element={<OrdersPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
