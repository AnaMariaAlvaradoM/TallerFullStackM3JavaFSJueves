import { HttpErrorResponse } from '@angular/common/http';

export function mensajeDeError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return 'No hay conexión con el servidor. Verifica que el backend esté encendido.';
    }
    if (error.status === 403) {
      return 'No tienes permiso para realizar esta acción.';
    }
    if (error.error && typeof error.error === 'object' && 'mensaje' in error.error && error.error.mensaje) {
      return String(error.error.mensaje);
    }
    if (typeof error.error === 'string' && error.error.trim().length > 0) {
      try {
        const json = JSON.parse(error.error);
        if (json && json.mensaje) return String(json.mensaje);
      } catch {
        return error.error;
      }
    }
    return `Error del servidor (${error.status}): no se pudo completar la operación`;
  }
  if (error && typeof error === 'object' && 'message' in error) {
    return String((error as { message: unknown }).message);
  }
  return 'Ha ocurrido un error inesperado';
}
