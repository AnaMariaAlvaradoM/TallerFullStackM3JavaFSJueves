import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { AuthService } from "./auth.service";
import { API_URL } from "./api";
import { catchError, throwError } from "rxjs";

export  const jwtInterceptor: HttpInterceptorFn = (req,next) =>{
    const authService = inject(AuthService);
    const token = authService.token();
    const esAlBackend = req.url.startsWith(API_URL);
    const esLogin = req.url.startsWith(`${API_URL}/auth/}`);

    const peticion = token && esAlBackend && !esLogin
    ?req.clone({setHeaders: {Authorization: `Bearer ${token}`}})
    :req;

    return next(peticion).pipe(
        catchError((error: HttpErrorResponse)=>{
            // 401 token: el token ya vencio o fe rechazado y eso causa que se cierre la sesión
            if(error.status ==401 && token && !esLogin){
                authService.cerrarSesion();
            }
            return throwError(()=> error);
        })
    );
};

