import React from 'react';
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import { useAuth } from '../context/AuthContext';

// Una sola pantalla para login y registro: <AuthPage modo="login" /> o modo="registro"
export default function AuthPage({ modo }) {
  const esRegistro = modo === 'registro';
  const [form, setForm] = useState({ nombre: '', email: '', password: '' });
  const [mensaje, setMensaje] = useState(null);
  const [enviando, setEnviando] = useState(false);
  const { iniciar } = useAuth();
  const navigate = useNavigate();

  const cambiar = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const enviar = async (e) => {
    e.preventDefault();
    setEnviando(true);
    setMensaje(null);
    try {
      const usuario = esRegistro
        ? await authService.registro(form)
        : await authService.login({ email: form.email, password: form.password });
      iniciar(usuario);
      navigate('/');
    } catch (error) {
      setMensaje(error.response?.data?.error || 'No se pudo completar la operación. Verifica ms-usuarios.');
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={enviar}>
        <a className="brand" href="/">● <span>Sell</span>Pilot</a>
        <h1>{esRegistro ? 'Crear cuenta' : 'Iniciar sesión'}</h1>
        <p className="auth-sub">{esRegistro ? 'Regístrate para empezar a comprar' : 'Ingresa para ver tu carrito y tus órdenes'}</p>

        {esRegistro && (
          <label className="auth-field">Nombre
            <input name="nombre" value={form.nombre} onChange={cambiar} placeholder="Tu nombre" required />
          </label>
        )}
        <label className="auth-field">Correo
          <input type="email" name="email" value={form.email} onChange={cambiar} placeholder="correo@ejemplo.com" required />
        </label>
        <label className="auth-field">Contraseña
          <input type="password" name="password" value={form.password} onChange={cambiar} placeholder="Mínimo 6 caracteres" minLength={6} required />
        </label>

        {mensaje && <p className="order-message error">{mensaje}</p>}
        <button className="payment-button" disabled={enviando}>
          {enviando ? 'Enviando...' : esRegistro ? 'Registrarme' : 'Entrar'}
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
