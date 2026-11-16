import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { API_URL } from './api';
import { AuthService } from './auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.token();
  const esAlBackend = req.url.startsWith(API_URL);
  const esLogin = req.url.startsWith(`${API_URL}/auth/`);

  const peticion = token && esAlBackend && !esLogin
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      // 401 con token = el token venció o fue rechazado: se cierra la sesión
      if (error.status === 401 && token && !esLogin) {
        authService.cerrarSesion();
      }
      return throwError(() => error);
    })
  );
};