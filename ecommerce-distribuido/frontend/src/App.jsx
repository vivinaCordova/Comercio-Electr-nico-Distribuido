import { Routes, Route } from 'react-router-dom';
import MainLayout from './components/layout/MainLayout.jsx';
import HomePage from './pages/HomePage.jsx';
import ProductsPage from './pages/ProductsPage.jsx';
import CartPage from './pages/CartPage.jsx';
import CheckoutPage from './pages/CheckoutPage.jsx';
import OrdersPage from './pages/OrdersPage.jsx';
export default function App(){return <Routes><Route element={<MainLayout/>}><Route index element={<HomePage/>}/><Route path="productos" element={<ProductsPage/>}/><Route path="carrito" element={<CartPage/>}/><Route path="checkout" element={<CheckoutPage/>}/><Route path="ordenes" element={<OrdersPage/>}/></Route></Routes>}
import React from 'react';