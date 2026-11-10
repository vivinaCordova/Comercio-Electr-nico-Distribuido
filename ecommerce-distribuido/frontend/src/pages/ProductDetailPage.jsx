import React, { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import Encabezado from '../components/layout/Encabezado.jsx';
import TarjetaProducto from '../components/products/TarjetaProducto.jsx';
import { useAuth } from '../context/AuthContext';
import { useCarrito } from '../context/CarritoContext';
import { catalogoService } from '../services/catalogoService';
import { aCentavos, formatoMoneda } from '../utils/dinero';
import { textoError } from '../utils/errores';
import imagenProducto from '../assets/imagen-producto.svg';

const srcImagen = (imagen) => imagen?.src || imagen?.url;

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { usuario } = useAuth();
  const { agregar, cantidadTotal } = useCarrito();
  const [producto, setProducto] = useState(null);
  const [relacionados, setRelacionados] = useState([]);
  const [seleccionada, setSeleccionada] = useState(null);
  const [cantidad, setCantidad] = useState(1);
  const [confirmado, setConfirmado] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    let vigente = true;
    setProducto(null); setError(null); setConfirmado(false);
    catalogoService.obtener(id).then(async (resultado) => {
      if (!vigente) return;
      setProducto(resultado);
      setCantidad(1);
      const imagenes = resultado.imagenes || [];
      setSeleccionada(srcImagen(imagenes.find((i) => i.esPrincipal) || imagenes[0]) || resultado.imagen);
      if (resultado.categoria?.slug) {
        const lista = await catalogoService.listar({ categoria: resultado.categoria.slug });
        if (vigente) setRelacionados(lista.filter((p) => String(p.id) !== String(id)).slice(0, 4));
      }
    }).catch((e) => vigente && setError(textoError(e, 'No pudimos encontrar este producto.')));
    return () => { vigente = false; };
  }, [id]);

  const imagenes = useMemo(() => producto?.imagenes?.length ? producto.imagenes : [{ src: producto?.imagen }], [producto]);
  const agregarAlCarrito = async () => {
    if (!usuario) {
      navigate('/login', { state: { from: `/productos/${id}` } });
      return;
    }
    if (await agregar(producto, cantidad)) setConfirmado(true);
  };
  const comprarAhora = async () => {
    await agregarAlCarrito();
    if (usuario) navigate('/checkout');
  };

  if (error) return <div className="shop-shell"><Encabezado /><div className="estado-vacio"><h1>{error}</h1><Link className="boton-naranja" to="/">Volver al inicio</Link></div></div>;
  if (!producto) return <div className="shop-shell"><Encabezado /><p className="empty-cart">Cargando producto...</p></div>;
  const disponible = Number(producto.stock) > 0;

  return (
    <div className="shop-shell">
      <Encabezado />
      <main className="product-detail">
        <Link className="back-link" to="/productos">← Volver al catálogo</Link>
        <div className="product-detail-grid">
          <div>
            <div className="product-detail-image">
              <img src={seleccionada || imagenProducto} alt={producto.nombre} loading="lazy" onError={(e) => { e.currentTarget.src = imagenProducto; }} />
            </div>
            <div className="product-thumbnails" aria-label="Galería de imágenes">
              {imagenes.map((imagen, index) => <button type="button" className={seleccionada === srcImagen(imagen) ? 'thumbnail activa' : 'thumbnail'} key={srcImagen(imagen) || index} onClick={() => setSeleccionada(srcImagen(imagen))}><img src={srcImagen(imagen) || imagenProducto} alt={`${producto.nombre}, vista ${index + 1}`} loading="lazy" onError={(e) => { e.currentTarget.src = imagenProducto; }} /></button>)}
            </div>
          </div>
          <section className="product-detail-info">
            <small className="categoria-label">{producto.categoria?.nombre || 'Producto'}</small>
            <h1>{producto.nombre}</h1>
            <strong className="product-detail-price">{formatoMoneda(aCentavos(producto.precio))}</strong>
            <p>{producto.descripcion || 'Producto disponible en nuestra tienda.'}</p>
            <p className={disponible ? 'stock-disponible' : 'stock-agotado'}>{disponible ? `Quedan ${producto.stock} unidades` : 'Sin stock'}</p>
            {disponible && <label className="quantity-picker">Cantidad <input type="number" min="1" max={producto.stock} value={cantidad} onChange={(e) => setCantidad(Math.max(1, Math.min(Number(e.target.value) || 1, producto.stock)))} /></label>}
            <div className="detail-actions"><button className="payment-button" onClick={comprarAhora} disabled={!disponible}>Comprar ahora</button><button className="secondary-button" onClick={agregarAlCarrito} disabled={!disponible}>Agregar al carrito</button></div>
            {confirmado && <p className="order-message success">Producto agregado. <Link to="/carrito">Ver carrito ({cantidadTotal})</Link></p>}
          </section>
        </div>
        <section className="product-extra"><h2>Descripción y especificaciones</h2><p>{producto.descripcion || 'Sin descripción adicional.'}</p><table><tbody><tr><th>Peso</th><td>{producto.pesoGramos ? `${producto.pesoGramos} g` : 'No especificado'}</td></tr><tr><th>Especificaciones</th><td>{producto.especificaciones || 'No especificadas'}</td></tr><tr><th>SKU</th><td>{producto.sku || 'No especificado'}</td></tr></tbody></table></section>
        {relacionados.length > 0 && <section className="related-products"><h2>También te puede interesar</h2><div className="product-grid">{relacionados.map((p) => <TarjetaProducto key={p.id} producto={p} onAgregar={agregar} />)}</div></section>}
      </main>
    </div>
  );
}
