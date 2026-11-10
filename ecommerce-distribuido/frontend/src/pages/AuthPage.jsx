import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import { useAuth } from '../context/AuthContext';
import Marca from '../components/layout/Marca.jsx';
import { limpiarTexto, normalizarEmail, validarNombre, validarEmail, validarPassword, validarConfirmacion, fuerzaPassword } from '../utils/validaciones';

const MAX_FALLOS = 5;
const BLOQUEO_SEG = 30;
const ETIQUETA_FUERZA = ['', 'Débil', 'Regular', 'Buena', 'Fuerte'];
const VACIO = { nombre: '', email: '', password: '', confirmar: '' };

function Campo({ etiqueta, error, children }) {
  return (
    <label className={error ? 'auth-field invalid' : 'auth-field'}>
      {etiqueta}
      {children}
      {error && <small className="auth-error" role="alert">{error}</small>}
    </label>
  );
}

// Una sola pantalla para login y registro: <AuthPage modo="login" /> o modo="registro"
export default function AuthPage({ modo }) {
  const esRegistro = modo === 'registro';
  const [form, setForm] = useState(VACIO);
  const [tocados, setTocados] = useState({});
  const [intentado, setIntentado] = useState(false);
  const [mensaje, setMensaje] = useState(null);
  const [enviando, setEnviando] = useState(false);
  const [mostrar, setMostrar] = useState(false);
  const [fallos, setFallos] = useState(0);
  const [espera, setEspera] = useState(0);
  const { iniciar } = useAuth();
  const navigate = useNavigate();

  // Al cambiar entre login y registro se limpia el formulario
  useEffect(() => { setForm(VACIO); setTocados({}); setIntentado(false); setMensaje(null); setMostrar(false); }, [modo]);

  // Cuenta regresiva del bloqueo temporal
  useEffect(() => {
    if (espera <= 0) return undefined;
    const t = setTimeout(() => setEspera((s) => s - 1), 1000);
    return () => clearTimeout(t);
  }, [espera]);

  const errores = {
    ...(esRegistro && { nombre: validarNombre(form.nombre) }),
    email: validarEmail(form.email),
    password: esRegistro ? validarPassword(form.password) : (form.password ? '' : 'Ingresa tu contraseña.'),
    ...(esRegistro && { confirmar: validarConfirmacion(form.password, form.confirmar) }),
  };
  const ver = (campo) => ((tocados[campo] || intentado) ? errores[campo] || '' : '');

  const cambiar = (e) => { const { name, value } = e.target; setForm((f) => ({ ...f, [name]: value })); setMensaje(null); };
  const salirCampo = (e) => setTocados((t) => ({ ...t, [e.target.name]: true }));
  const props = (campo) => ({ name: campo, value: form[campo], onChange: cambiar, onBlur: salirCampo, 'aria-invalid': !!ver(campo) });

  const enviar = async (e) => {
    e.preventDefault();
    if (enviando || espera > 0) return;
    setIntentado(true);
    if (Object.values(errores).some(Boolean)) {
      setTimeout(() => document.querySelector('.auth-field.invalid input')?.focus(), 0);
      return;
    }
    setEnviando(true);
    setMensaje(null);
    try {
      const datos = esRegistro
        ? { nombre: limpiarTexto(form.nombre), email: normalizarEmail(form.email), password: form.password }
        : { email: normalizarEmail(form.email), password: form.password };
      const usuario = esRegistro ? await authService.registro(datos) : await authService.login(datos);
      iniciar(usuario);
      navigate('/');
    } catch (error) {
      const estado = error.response?.status;
      setMensaje(error.response?.data?.error
        || (estado === 401 ? 'Correo o contraseña incorrectos.'
          : estado === 409 ? 'Ese correo ya está registrado.'
            : 'No se pudo completar la operación. Intenta de nuevo.'));
      const total = fallos + 1;
      if (total >= MAX_FALLOS) { setFallos(0); setEspera(BLOQUEO_SEG); setMensaje('Demasiados intentos fallidos. Espera un momento.'); } else setFallos(total);
    } finally {
      setEnviando(false);
    }
  };

  const nivel = fuerzaPassword(form.password);

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={enviar} noValidate>
        <Marca />
        <h1>{esRegistro ? 'Crear cuenta' : 'Iniciar sesión'}</h1>
        <p className="auth-sub">{esRegistro ? 'Regístrate para empezar a comprar' : 'Ingresa para ver tu carrito y tus órdenes'}</p>

        {esRegistro && (
          <Campo etiqueta="Nombre completo" error={ver('nombre')}>
            <input {...props('nombre')} maxLength={50} autoComplete="name" placeholder="Tu nombre" />
          </Campo>
        )}
        <Campo etiqueta="Correo electrónico" error={ver('email')}>
          <input {...props('email')} type="email" maxLength={254} autoComplete="email" placeholder="correo@ejemplo.com" />
        </Campo>
        <Campo etiqueta="Contraseña" error={ver('password')}>
          <div className="auth-input-wrap">
            <input {...props('password')} type={mostrar ? 'text' : 'password'} maxLength={64} autoComplete={esRegistro ? 'new-password' : 'current-password'} placeholder={esRegistro ? 'Mínimo 8 caracteres' : 'Tu contraseña'} />
            <button type="button" className="auth-toggle" onClick={() => setMostrar(!mostrar)} aria-label={mostrar ? 'Ocultar contraseña' : 'Mostrar contraseña'}>{mostrar ? 'Ocultar' : 'Mostrar'}</button>
          </div>
          {esRegistro && form.password && (
            <div className="strength" data-nivel={nivel}><i /><i /><i /><i /><span>{ETIQUETA_FUERZA[nivel]}</span></div>
          )}
        </Campo>
        {esRegistro && (
          <Campo etiqueta="Confirmar contraseña" error={ver('confirmar')}>
            <input {...props('confirmar')} type={mostrar ? 'text' : 'password'} maxLength={64} autoComplete="new-password" placeholder="Repite tu contraseña" />
          </Campo>
        )}

        {mensaje && <p className="order-message error" role="alert">{mensaje}</p>}
        <button className="payment-button" disabled={enviando || espera > 0}>
          {espera > 0 ? `Espera ${espera}s` : enviando ? 'Enviando...' : esRegistro ? 'Registrarme' : 'Entrar'}
        </button>

        <p className="auth-sub">
          {esRegistro
            ? <>¿Ya tienes cuenta? <Link className="auth-link" to="/login">Inicia sesión</Link></>
            : <>¿No tienes cuenta? <Link className="auth-link" to="/registro">Regístrate</Link></>}
        </p>
      </form>
    </div>
  );
}
