import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';
import { Permiso } from './permisos';

export function permisoGuard(permiso: Permiso): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (auth.puede(permiso)) {
      return true;
    }

    return router.createUrlTree(['/sin-acceso']);
  };
}