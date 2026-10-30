import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "./auth.service";

// Es el guarda que exige verificación para dejar pasar y si no tiene verificación se dirige a login.
export const authGuard: CanActivateFn = () =>{
    const authService = inject(AuthService);
    const router = inject(Router);

    return authService.sesionActiva() ? true :router.createUrlTree(['/login'])
}
