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

const variantesDelProducto = (producto) => {
  let fuente = producto?.variantes || producto?.opciones || {};
  if (typeof fuente === 'string') {
    try {
      fuente = JSON.parse(fuente);
    } catch {
      fuente = {};
    }
  }
  if (!Object.keys(fuente).length && typeof producto?.especificaciones === 'string') {
    try {
      fuente = JSON.parse(producto.especificaciones);
    } catch {
      fuente = {};
    }
  }
  if (Array.isArray(fuente)) {
    return fuente.reduce((resultado, variante) => {
      const nombre = variante.nombre || variante.tipo || variante.label;
      const opciones = variante.opciones || variante.valores || variante.values;
      if (nombre && Array.isArray(opciones) && opciones.length) resultado[nombre] = opciones;
      return resultado;
    }, {});
  }
  return Object.entries(fuente).reduce((resultado, [nombre, opciones]) => {
    if (Array.isArray(opciones) && opciones.length) resultado[nombre] = opciones;
    return resultado;
  }, {});
};

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { usuario } = useAuth();
  const { agregar, cantidadTotal } = useCarrito();
  const [producto, setProducto] = useState(null);
  const [relacionados, setRelacionados] = useState([]);
  const [seleccionada, setSeleccionada] = useState(null);
  const [cantidad, setCantidad] = useState(1);
  const [variantes, setVariantes] = useState({});
  const [confirmado, setConfirmado] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    let vigente = true;
    setProducto(null); setError(null); setConfirmado(false);
    catalogoService.obtener(id).then(async (resultado) => {
      if (!vigente) return;
      setProducto(resultado);
      setCantidad(1);
      setVariantes({});
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
  const opcionesVariantes = useMemo(() => variantesDelProducto(producto), [producto]);
  const especificaciones = useMemo(() => {
    if (!producto?.especificaciones) return [];
    try {
      const datos = JSON.parse(producto.especificaciones);
      return Object.entries(datos).slice(0, 10);
    } catch {
      return [];
    }
  }, [producto]);
  const agregarAlCarrito = async () => {
    if (!usuario) {
      navigate('/login', { state: { from: `/productos/${id}` } });
      return;
    }
    const itemParaCarrito = {
      productId: producto.id,
      cantidad,
      variantes,
    };
    if (await agregar(producto, cantidad, itemParaCarrito.variantes)) setConfirmado(true);
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
        <div className="product-detail-grid items-start">
          <div className="grid gap-3">
            <div className="product-detail-image min-h-0 aspect-square lg:aspect-[4/3]">
              <img src={seleccionada || imagenProducto} alt={producto.nombre} loading="lazy" onError={(e) => { e.currentTarget.src = imagenProducto; }} />
            </div>
            <div className="product-thumbnails flex-nowrap overflow-x-auto pb-1" aria-label="Galería de imágenes">
              {imagenes.map((imagen, index) => <button type="button" className={seleccionada === srcImagen(imagen) ? 'thumbnail activa ring-2 ring-orange-400' : 'thumbnail'} key={srcImagen(imagen) || index} onClick={() => setSeleccionada(srcImagen(imagen))} aria-label={`Ver imagen ${index + 1}`} aria-pressed={seleccionada === srcImagen(imagen)}><img src={srcImagen(imagen) || imagenProducto} alt={`${producto.nombre}, vista ${index + 1}`} loading="lazy" onError={(e) => { e.currentTarget.src = imagenProducto; }} /></button>)}
            </div>
          </div>
          <section className="product-detail-info rounded-2xl border border-orange-100 bg-white p-5 shadow-sm lg:p-7">
            <small className="categoria-label">{producto.categoria?.nombre || 'Producto'}</small>
            <h1 className="!text-3xl !leading-tight lg:!text-4xl">{producto.nombre}</h1>
            <strong className="product-detail-price !mb-4">{formatoMoneda(aCentavos(producto.precio))}</strong>
            <p className="!mt-0">{producto.descripcion || 'Producto disponible en nuestra tienda.'}</p>
            <p className={disponible ? 'stock-disponible' : 'stock-agotado'}>{disponible ? `Quedan ${producto.stock} unidades` : 'Sin stock'}</p>
            {Object.entries(opcionesVariantes).map(([nombre, opciones]) => (
              <fieldset className="mt-5" key={nombre}>
                <legend className="mb-2 text-sm font-bold text-gray-800">{nombre}</legend>
                <div className="flex flex-wrap gap-2">
                  {opciones.map((opcion) => {
                    const valor = typeof opcion === 'object' ? (opcion.nombre || opcion.label || opcion.valor) : opcion;
                    const activo = variantes[nombre] === valor;
                    return <button type="button" key={valor} className={`rounded-lg border px-4 py-2 text-sm transition ${activo ? 'border-orange-500 bg-orange-50 font-bold text-orange-700 ring-2 ring-orange-200' : 'border-gray-200 bg-white text-gray-700 hover:border-orange-300'}`} onClick={() => setVariantes((actuales) => ({ ...actuales, [nombre]: valor }))} aria-pressed={activo}>{valor}</button>;
                  })}
                </div>
              </fieldset>
            ))}
            {disponible && <label className="quantity-picker mt-5">Cantidad <input type="number" min="1" max={producto.stock} value={cantidad} onChange={(e) => setCantidad(Math.max(1, Math.min(Number(e.target.value) || 1, producto.stock)))} /></label>}
            <div className="detail-actions flex-col sm:flex-row"><button className="payment-button" onClick={comprarAhora} disabled={!disponible}>Comprar ahora</button><button className="secondary-button" onClick={agregarAlCarrito} disabled={!disponible}>Agregar al carrito</button></div>
            {confirmado && <p className="order-message success">Producto agregado. <Link to="/carrito">Ver carrito ({cantidadTotal})</Link></p>}
          </section>
        </div>
        {producto.descripcion && <section className="product-extra"><h2>Descripción</h2><p>{producto.descripcion}</p></section>}
        {(producto.pesoGramos || especificaciones.length > 0) && (
          <section className="product-extra">
            <h2>Características</h2>
            <table><tbody>
              {producto.pesoGramos && <tr><th>Peso</th><td>{producto.pesoGramos >= 1000 ? `${(producto.pesoGramos / 1000).toFixed(2)} kg` : `${producto.pesoGramos} g`}</td></tr>}
              {especificaciones.map(([clave, valor]) => <tr key={clave}><th>{clave}</th><td>{valor}</td></tr>)}
            </tbody></table>
          </section>
        )}
        {relacionados.length > 0 && <section className="related-products"><h2>También te puede interesar</h2><div className="product-grid">{relacionados.map((p) => <TarjetaProducto key={p.id} producto={p} onAgregar={agregar} />)}</div></section>}
      </main>
    </div>
  );
}
