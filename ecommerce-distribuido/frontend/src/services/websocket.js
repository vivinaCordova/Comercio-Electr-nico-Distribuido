// Preparación de integración WebSocket: el navegador contacta al Gateway HTTP para REST.
export const notificationsWebSocketUrl = import.meta.env.VITE_NOTIFICATIONS_WS_URL || 'ws://localhost:8084/ws/notificaciones';
