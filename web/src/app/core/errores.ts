import { HttpErrorResponse } from '@angular/common/http';

// Traduce un error HTTP a un mensaje claro para quien usa Colmena.
// El backend responde { codigo, mensaje, fecha }: si trae mensaje, se usa ese.
export function mensajeDeError(error: HttpErrorResponse): string {
  if (error.status === 0) {
    return 'No pudimos conectarnos con el servidor. Verifica que el backend esté corriendo en el puerto 8080.';
  }
  if (error.status === 403) {
    return 'No tienes permiso para realizar esta acción.';
  }
  return error.error?.mensaje ?? 'Ocurrió un error inesperado. Intenta de nuevo.';
}