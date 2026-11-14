import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { AuthService } from "./auth.service";
import { API_URL } from "./api";
import { catchError, throwError } from "rxjs";

//Token actual y verifica: ¿va al backend? ¿ es el login?
export  const jwtInterceptor: HttpInterceptorFn = (req,next) =>{
    const authService = inject(AuthService);
    const token = authService.token();
    const esAlBackend = req.url.startsWith(API_URL);
    const esLogin = req.url.startsWith(`${API_URL}/auth/}`);

    // si hay token, va al backend y no es el login
    const peticion = token && esAlBackend && !esLogin
    ?req.clone({setHeaders: {Authorization: `Bearer ${token}`}})
    :req; //las peticiones son inmutables: se crea una copia

    return next(peticion).pipe( // si el backend rechaza el token, se cierra la sesion y el error sigue su camino en el service
        catchError((error: HttpErrorResponse)=>{
            // 401 token: el token ya vencio o fe rechazado y eso causa que se cierre la sesión
            if(error.status ==401 && token && !esLogin){
                authService.cerrarSesion();
            }
            return throwError(()=> error);
        })
    );
};

