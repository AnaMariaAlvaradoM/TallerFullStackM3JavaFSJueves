import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Permiso } from './permisos';

// Una "fábrica" de guards: permisoGuard('CLIENTES_GESTIONAR') crea un guard para ese permiso
export function permisoGuard(permiso: Permiso): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    return auth.puede(permiso) ? true : router.createUrlTree(['/sin-acceso']);
  };
}
