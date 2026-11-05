import { HttpErrorResponse } from '@angular/common/http';

// Traduce un error HTTP a un texto que la persona entienda
export function mensajeDeError(error: HttpErrorResponse): string {
  // status 0 = ni siquiera hubo respuesta: el backend está apagado o no hay red
  if (error.status === 0) {
    return 'No hay conexión con el servidor. Revisa que el backend esté encendido.';
  }

  if (error.status === 403) {
    return 'No tienes permiso para realizar esta acción.';
  }

  // El backend siempre responde { codigo, mensaje, fecha }: usamos su mensaje tal cual
  const mensaje = error.error?.mensaje;
  if (typeof mensaje === 'string' && mensaje.trim().length > 0) {
    return mensaje;
  }

  return 'Ocurrió un error inesperado. Intenta de nuevo.';
}
