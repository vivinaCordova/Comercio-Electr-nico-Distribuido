// Validaciones reutilizables para todos los formularios (una función pura por campo).
// Devuelven '' si el valor es válido, o el mensaje de error en español.
const DESECHABLES = ['mailinator.com', 'tempmail.com', '10minutemail.com', 'guerrillamail.com', 'yopmail.com', 'trashmail.com', 'sharklasers.com'];
const ERRORES_COMUNES = { 'gmial.com': 'gmail.com', 'gmai.com': 'gmail.com', 'gamil.com': 'gmail.com', 'gmail.con': 'gmail.com', 'hotmial.com': 'hotmail.com', 'hotmail.con': 'hotmail.com', 'outlok.com': 'outlook.com', 'yahoo.con': 'yahoo.com' };
const CLAVES_DEBILES = ['12345678', '123456789', 'password', 'password1', 'contrasena', 'contraseña', 'qwerty123', 'abc12345'];

// Quita caracteres de control y < > (evita basura/inyección en textos libres) y espacios repetidos
export const limpiarTexto = (valor) => valor.replace(/[\u0000-\u001F\u007F<>]/g, '').replace(/\s+/g, ' ').trim();
export const normalizarEmail = (valor) => valor.trim().toLowerCase();

export const validarNombre = (valor) => {
  const v = limpiarTexto(valor);
  if (!v) return 'Ingresa tu nombre.';
  if (v.length < 2) return 'El nombre es muy corto.';
  if (v.length > 50) return 'El nombre no puede superar 50 caracteres.';
  if (!/^[\p{L}][\p{L}\s'.-]*$/u.test(v)) return 'Usa solo letras, espacios, apóstrofes o guiones.';
  return '';
};

export const validarEmail = (valor) => {
  const v = normalizarEmail(valor);
  if (!v) return 'Ingresa tu correo.';
  if (v.length > 254) return 'El correo es demasiado largo.';
  if (!/^[a-z0-9]+([._%+-][a-z0-9]+)*@[a-z0-9]+(-[a-z0-9]+)*(\.[a-z0-9]+(-[a-z0-9]+)*)*\.[a-z]{2,}$/.test(v)) return 'El formato del correo no es válido.';
  const [usuario, dominio] = v.split('@');
  if (usuario.length > 64) return 'La parte anterior al @ es demasiado larga.';
  if (ERRORES_COMUNES[dominio]) return `¿Quisiste decir ${usuario}@${ERRORES_COMUNES[dominio]}?`;
  if (DESECHABLES.includes(dominio)) return 'No se permiten correos temporales.';
  return '';
};

export const validarPassword = (v) => {
  if (!v) return 'Ingresa una contraseña.';
  if (v.length < 8) return 'Mínimo 8 caracteres.';
  if (v.length > 64) return 'Máximo 64 caracteres.';
  if (/\s/.test(v)) return 'No uses espacios.';
  if (!/[a-z]/.test(v) || !/[A-Z]/.test(v)) return 'Incluye mayúsculas y minúsculas.';
  if (!/\d/.test(v)) return 'Incluye al menos un número.';
  if (CLAVES_DEBILES.includes(v.toLowerCase())) return 'Esa contraseña es muy común.';
  return '';
};

export const validarConfirmacion = (password, confirmar) => {
  if (!confirmar) return 'Confirma tu contraseña.';
  return password !== confirmar ? 'Las contraseñas no coinciden.' : '';
};

// 0 a 4, para la barra de fortaleza
export const fuerzaPassword = (v) => {
  let n = 0;
  if (v.length >= 8) n++;
  if (/[a-z]/.test(v) && /[A-Z]/.test(v)) n++;
  if (/\d/.test(v)) n++;
  if (/[^A-Za-z0-9]/.test(v) || v.length >= 12) n++;
  return n;
};
