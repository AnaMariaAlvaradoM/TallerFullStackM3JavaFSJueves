import { CanActivateFn,Router } from "@angular/router";
import { Permiso } from "./permisos"
import { inject } from "@angular/core";
import { AuthService } from "./auth.service";

// Este es el guarda que sabe que es lo que puede hacer y muestra las rutas a las que tiene acceso a routerOutlet
export function permisoGuard(permiso:Permiso):CanActivateFn{
    return () =>{
        const authService = inject(AuthService);
        const router = inject(Router);
        return authService.puede(permiso)?true : router.createUrlTree(['/sin-acceso']);
    };
}
