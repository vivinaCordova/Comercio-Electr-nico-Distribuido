import { useMemo, useState } from 'react';
import React from 'react';
import { ordenesService } from '../services/ordenesService';

const products = [
  { id: 1, title: 'iPhone 17 Pro', category: 'Mobile', price: 1340, oldPrice: 1400, image: 'https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=500&q=80' },
  { id: 2, title: 'vivo X300 5G', category: 'Mobile', price: 780, oldPrice: 825, image: 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500&q=80' },
  { id: 3, title: 'Samsung Galaxy S25 Ultra', category: 'Mobile', price: 1250, oldPrice: 1350, image: 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500&q=80' },
  { id: 4, title: 'iPhone 17', category: 'Mobile', price: 780, oldPrice: 820, image: 'https://images.unsplash.com/photo-1601784551446-20c9e07cdbdb?w=500&q=80' },
  { id: 5, title: 'iPhone Air', category: 'Mobile', price: 890, oldPrice: 930, image: 'https://images.unsplash.com/photo-1556656793-08538906a9f8?w=500&q=80' },
  { id: 6, title: 'OnePlus 13', category: 'Mobile', price: 750, oldPrice: 800, image: 'https://images.unsplash.com/photo-1605236453806-6ff36851218e?w=500&q=80' },
  { id: 7, title: 'Google Pixel 9', category: 'Mobile', price: 690, oldPrice: 740, image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&q=80' },
  { id: 8, title: 'Redmi Note 14', category: 'Mobile', price: 420, oldPrice: 460, image: 'https://images.unsplash.com/photo-1592286927505-2fd8b6f1c2a3?w=500&q=80' },
];

const categories = ['Mobile', 'All Products', 'Grocery', 'Most Purchased', 'Shoes', 'Furniture'];
const money = (value) => `$${value.toLocaleString('en-US')}`;

export default function HomePage() {
  const [cart, setCart] = useState([{ ...products[0], quantity: 1 }, { ...products[1], quantity: 1 }, { ...products[2], quantity: 1 }]);
  const [category, setCategory] = useState('Mobile');
  const [query, setQuery] = useState('');
  const [promo, setPromo] = useState('');
  const [message, setMessage] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const visibleProducts = useMemo(() => products.filter((product) =>
    (category === 'All Products' || product.category === category) &&
    product.title.toLowerCase().includes(query.toLowerCase())
  ), [category, query]);

  const subtotal = cart.reduce((total, item) => total + item.price * item.quantity, 0);
  const discount = promo.trim().toUpperCase() === 'SELL12' ? Math.round(subtotal * 0.12) : 0;
  const total = subtotal - discount;

  const addToCart = (product) => setCart((current) => {
    const existing = current.find((item) => item.id === product.id);
    return existing
      ? current.map((item) => item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item)
      : [...current, { ...product, quantity: 1 }];
  });

  const changeQuantity = (id, amount) => setCart((current) => current
    .map((item) => item.id === id ? { ...item, quantity: item.quantity + amount } : item)
    .filter((item) => item.quantity > 0));

  const createOrder = async () => {
    if (!cart.length) {
      setMessage({ type: 'error', text: 'Agrega al menos un producto al carrito.' });
      return;
    }
    setSubmitting(true);
    setMessage(null);
    try {
      await ordenesService.crear({
        userId: 1,
        items: cart.map(({ id, quantity, price }) => ({
          productId: id,
          quantity,
          precioEsperado: price,
        })),
      });
      setMessage({ type: 'success', text: 'Orden creada correctamente.' });
    } catch (error) {
      const detail = error.response?.data?.error || 'No se pudo crear la orden. Verifica ms-ordenes y ms-catalogo.';
      setMessage({ type: 'error', text: detail });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="shop-shell">
      <header className="shop-header">
        <a className="brand" href="/">● <span>Sell</span>Pilot</a>
        <nav className="shop-nav">
          <a href="/">▦ <span>Dashboard</span></a>
          <a href="#products">◈ <span>Products</span></a>
          <a href="#orders">♧ <span>My Orders</span></a>
          <a href="#wishlist">♡ <span>Wishlist</span></a>
          <a className="active" href="#cart">🛒 <span>Cart</span></a>
          <a href="#settings">⚙ <span>Settings</span></a>
        </nav>
        <div className="profile-actions"><button aria-label="Notifications">♧</button><button aria-label="Wishlist">♡</button><span className="avatar">C</span></div>
      </header>

      <div className="shop-content">
        <section className="products-column" id="products">
          <div className="section-heading">
            <h1>Cart Products</h1>
            <div className="heading-actions">
              <label className="search-box">⌕<input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search..." /></label>
              <button className="filter-button">☷ <span>Filter</span></button>
              <button className="create-button" onClick={() => document.getElementById('cart')?.scrollIntoView({ behavior: 'smooth' })}>＋ Create Order</button>
            </div>
          </div>
          <div className="category-list">{categories.map((item) => <button key={item} className={category === item ? 'category active' : 'category'} onClick={() => setCategory(item)}>{item}</button>)}</div>
          <div className="product-grid">
            {visibleProducts.map((product) => <article className="product-card" key={product.id}>
              <div className="product-image"><img src={product.image} alt={product.title} /><button aria-label={`Favorito ${product.title}`}>♡</button></div>
              <h2>{product.title}</h2>
              <div className="product-footer"><div><strong>{money(product.price)}</strong> <del>{money(product.oldPrice)}</del></div><button className="add-button" onClick={() => addToCart(product)} aria-label={`Agregar ${product.title}`}>↗</button></div>
            </article>)}
          </div>
        </section>

        <aside className="cart-panel" id="cart">
          <div className="cart-title"><h1>Cart</h1><button onClick={() => setCart([])}>Delete All</button></div>
          <div className="cart-items">
            {cart.length ? cart.map((item) => <div className="cart-item" key={item.id}>
              <span className="selected">✓</span><img src={item.image} alt="" /><div className="cart-item-info"><strong>{item.title}</strong><small>Silver</small><div className="quantity"><button onClick={() => changeQuantity(item.id, -1)}>−</button><span>{item.quantity}</span><button onClick={() => changeQuantity(item.id, 1)}>＋</button></div></div><button className="remove-item" onClick={() => changeQuantity(item.id, -item.quantity)} aria-label={`Eliminar ${item.title}`}>♧</button><b>{money(item.price * item.quantity)}</b>
            </div>) : <p className="empty-cart">Tu carrito está vacío.</p>}
          </div>
          <div className="promo"><span>◉ &nbsp; Promo Every User (12%)</span><input value={promo} onChange={(event) => setPromo(event.target.value)} placeholder="SELL12" /><button onClick={() => setPromo('SELL12')}>Change Promo</button></div>
          <div className="summary"><div><span>Total Product Price</span><b>{money(subtotal)}</b></div><div><span>Discount</span><b className="discount">−{money(discount)}</b></div><div className="total-row"><span>Total Payment</span><b>{money(total)}</b></div></div>
          {message && <p className={`order-message ${message.type}`}>{message.text}</p>}
          <button className="payment-button" onClick={createOrder} disabled={submitting}>{submitting ? 'Creating order...' : 'Proceed to Payment'}</button>
        </aside>
      </div>
    </div>
  );
}
