import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Permiso } from './permisos';

// Una "fábrica" de guards: recibe el permiso que exige la ruta
// y devuelve el guard que lo verifica. Uso: permisoGuard('USUARIOS_GESTIONAR')
export function permisoGuard(permiso: Permiso): CanActivateFn {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    return authService.puede(permiso) ? true : router.createUrlTree(['/sin-acceso']);
  };
}