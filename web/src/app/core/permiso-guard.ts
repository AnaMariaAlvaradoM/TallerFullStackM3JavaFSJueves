import { CanActivateFn,Router } from "@angular/router";
import { Permiso } from "./permisos"
import { inject } from "@angular/core";
import { AuthService } from "./auth.service";
export function PermisoGuard(permiso:Permiso):CanActivateFn{
    return () =>{
        const authService = inject(AuthService);
        const router = inject(Router);
        return authService.puede(permiso)?true : router.createUrlTree(['/sin-acceso']);
    };
}
