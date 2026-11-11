// Convierte un error de red o de API en un texto legible (nunca HTML ni "Internal Server Error" crudo)
const GENERICOS = ['Internal Server Error', 'Bad Request', 'Not Found', 'Unprocessable Entity'];

export function textoError(error, porDefecto = 'Ocurrió un error. Intenta de nuevo.') {
  if (error.code === 'ECONNABORTED') return 'El servidor tardó demasiado en responder. Intenta de nuevo.';
  if (!error.response) return 'No hay conexión con el servidor. Verifica que el Gateway (puerto 8080) esté activo.';

  const data = error.response.data;
  let mensaje = null;
  if (typeof data === 'string' && !data.trim().startsWith('<')) mensaje = data.trim();
  else if (data && typeof data === 'object') mensaje = data.error || data.message;

  return mensaje && !GENERICOS.includes(mensaje) ? mensaje : porDefecto;
}
