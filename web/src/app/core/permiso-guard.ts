import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Permiso } from './permisos';



export function permisoGuard(permiso: Permiso): CanActivateFn {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    return authService.puede(permiso) ? true : router.createUrlTree(['/sin-acceso']);
  };
}