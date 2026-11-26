import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const permissionGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const permisoRequerido = route.data['permiso'] as string;

  if (authService.tienePermiso(permisoRequerido)) {
    return true;
  }

  void router.navigate(['/unauthorized']);
  return false;
};
