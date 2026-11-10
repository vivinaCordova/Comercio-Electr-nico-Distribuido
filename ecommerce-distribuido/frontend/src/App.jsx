import React from 'react';
import { Routes, Route } from 'react-router-dom';
import MainLayout from './components/layout/MainLayout.jsx';
import RutaProtegida from './components/layout/RutaProtegida.jsx';
import AuthPage from './pages/AuthPage.jsx';
import HomePage from './pages/HomePage.jsx';
import ProductsPage from './pages/ProductsPage.jsx';
import CartPage from './pages/CartPage.jsx';
import CheckoutPage from './pages/CheckoutPage.jsx';
import OrdersPage from './pages/OrdersPage.jsx';

export default function App() {
  return (
    <Routes>
      <Route path="login" element={<AuthPage modo="login" />} />
      <Route path="registro" element={<AuthPage modo="registro" />} />
      <Route element={<RutaProtegida><MainLayout /></RutaProtegida>}>
        <Route index element={<HomePage />} />
        <Route path="productos" element={<ProductsPage />} />
        <Route path="carrito" element={<CartPage />} />
        <Route path="checkout" element={<CheckoutPage />} />
        <Route path="ordenes" element={<OrdersPage />} />
      </Route>
    </Routes>
  );
}
