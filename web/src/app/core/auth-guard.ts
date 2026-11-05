import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

// Sin sesión no se entra: devuelve un UrlTree que manda a /login
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.sesionActiva() ? true : router.createUrlTree(['/login']);
};
