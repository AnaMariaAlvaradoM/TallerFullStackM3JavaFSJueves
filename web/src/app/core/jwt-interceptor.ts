import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { API_URL } from './api';

// Se mete en CADA petición HTTP: si hay sesión, le pega el token al encabezado
export const jwtInterceptor: HttpInterceptorFn = (peticion, siguiente) => {
  const auth = inject(AuthService);
  const token = auth.token();

  if (token && auth.sesionActiva() && peticion.url.startsWith(API_URL)) {
    peticion = peticion.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }

  return siguiente(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      // 401 con sesión abierta = el token ya no sirve (venció o Hernán bloqueó la cuenta)
      if (error.status === 401 && token && !peticion.url.endsWith('/auth/login')) {
        auth.cerrarSesion();
      }
      return throwError(() => error);
    }),
  );
};
