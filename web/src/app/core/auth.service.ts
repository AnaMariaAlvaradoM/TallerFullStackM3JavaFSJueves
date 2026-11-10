import { signal,computed, inject, Injectable} from '@angular/core';
import { Permiso,Rol } from './permisos';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import {mensajeDeError} from './errores';
import { API_URL } from './api';

// defina la forma del objeto que se envia al login
export interface Credenciales{
    email: string;
    password: string;
}

//Es la forma de lo que responde el backend: un objeto con el JWT. No se exporta porque solo se usa en el service
interface RespuestaAuth{
    token: string
}

/* Describe lo que hay dentro del payload del JWT.*/
interface ContenidoToken{
    sub: string;               //Identificador del usuario
    nombre: string;
    rol: Rol;
    // arreglo de permisos
    permisos: Permiso[];
    exp: number; //expiración, en segundos
}

// nombre de la llave con la que se guardan las token en locarStorage.
const llave_token = 'llave_token'


function leerToken(token:string): ContenidoToken | null{
    try{
        //Divide el token en tres partes y toma el payload
        const payload = token.split('.')[1];

        //base 64 convierte base64url de JWT y lo convierte en base64; ( - y _ ) => ( + y /). 
        const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');

        //atob decodifica base64 a un string binario. Convierte cada crácter a su número de byte , para luego obtener un arreglo de bytes
        const bytes = Uint8Array.from(atob(base64), letra =>letra.charCodeAt(0));

        //TextDecoder interpreta los bytes como UTF-8 y JSON.parse los convierte en objeto
        return JSON.parse(new TextDecoder().decode(bytes));
    } catch{
        //Si el token está mall formado, devuelve un null
        return null;
    }
}

@Injectable({providedIn: 'root'})
export class AuthService {

    private http = inject(HttpClient);
    private router = inject(Router);

    // Al recargar la página, la sesión sigue gracias a localStorage
    private tokenSignal = signal<string | null>(localStorage.getItem(llave_token));
    private errorSignal = signal<string | null>(null);
    private cargandoSignal = signal(false);

    private contenido = computed(() =>{
        const token = this.tokenSignal();
        return token ? leerToken(token) : null;
    });

    sesionActiva = computed(()=>{
        const contenido = this.contenido();
        return contenido != null && contenido.exp * 1000 > Date.now();
    });

    //Esto es lo que la app puede leer: nombre, email, rol  y permiso, mas los computed readOnly del  estado.
    nombre = computed(() => (this.sesionActiva()?this.contenido()!.nombre : ''));
    email = computed(() => (this.sesionActiva()?this.contenido()!.sub : ''));
    rol = computed(() => (this.sesionActiva()?this.contenido()!.rol : ''));
    permisos = computed(() => (this.sesionActiva()?this.contenido()!.permisos : []));

    token = this.tokenSignal.asReadonly();
    error = this.errorSignal.asReadonly();
    cargando = this.cargandoSignal.asReadonly();

    // Esta es la que decide que puede hacer cada usuario y determina que se muestra en pantall 
    puede(permiso: Permiso): boolean {
        return this.permisos().includes(permiso);
      }
    
    iniciarSesion(credenciales: Credenciales){
        this.errorSignal.set(null);
        this.cargandoSignal.set(true);

        this.http.post<RespuestaAuth>(`${API_URL}/auth/login`, credenciales).subscribe({
            next: (respuesta) => {
                this.tokenSignal.set(respuesta.token);
                localStorage.setItem(llave_token, respuesta.token);
                this.cargandoSignal.set(true);
                this.router.navigateByUrl('/herramientas');
            },
            error: (error: HttpErrorResponse) => {
                this.cargandoSignal.set(false);
                this.errorSignal.set(mensajeDeError(error));
            }
        });
    }

    cerrarSesion(){
        this.tokenSignal.set(null);
        localStorage.removeItem(llave_token);
        this.router.navigateByUrl('/login');
    }
}
