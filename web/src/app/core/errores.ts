import { HttpErrorResponse } from '@angular/common/http';

export function mensajeDeError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return 'No fue posible conectar con el servidor. Verifica que el backend esté encendido.';
    }

    if (error.status === 403) {
      return 'No tienes permiso para realizar esta acción.';
    }

    const cuerpo = error.error as { mensaje?: unknown } | string | null;
    if (cuerpo && typeof cuerpo === 'object' && typeof cuerpo.mensaje === 'string' && cuerpo.mensaje.trim()) {
      return cuerpo.mensaje.trim();
    }

    if (typeof error.message === 'string' && error.message.trim()) {
      return error.message.trim();
    }
  }

  return 'Ocurrió un error inesperado. Intenta nuevamente.';
}