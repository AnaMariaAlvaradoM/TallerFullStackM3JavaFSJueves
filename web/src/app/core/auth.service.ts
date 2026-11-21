import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { API_URL } from './api';
import { mensajeDeError } from './errores';
import { Permiso, Rol } from './permisos';

export interface Credenciales {
  email: string;
  password: string;
}

interface RespuestaAuth {
  token: string;
}


interface ContenidoToken {
  sub: string;
  nombre: string;
  rol: Rol;
  permisos: Permiso[];
  exp: number;
}

const CLAVE_TOKEN = 'llave_token';


function leerToken(token: string): ContenidoToken | null {
  try {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const bytes = Uint8Array.from(atob(base64), letra => letra.charCodeAt(0));
    return JSON.parse(new TextDecoder().decode(bytes));
  } catch {
    return null;
  }
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private tokenSignal = signal<string | null>(localStorage.getItem(CLAVE_TOKEN));
  private errorSignal = signal<string | null>(null);
  private cargandoSignal = signal(false);

  private contenido = computed(() => {
    const token = this.tokenSignal();
    return token ? leerToken(token) : null;
  });

  sesionActiva = computed(() => {
    const contenido = this.contenido();
    return contenido !== null && contenido.exp * 1000 > Date.now();
  });

  nombre = computed(() => (this.sesionActiva() ? this.contenido()!.nombre : ''));
  email = computed(() => (this.sesionActiva() ? this.contenido()!.sub : ''));
  rol = computed(() => (this.sesionActiva() ? this.contenido()!.rol : null));
  permisos = computed(() => (this.sesionActiva() ? this.contenido()!.permisos : []));

  token = this.tokenSignal.asReadonly();
  error = this.errorSignal.asReadonly();
  cargando = this.cargandoSignal.asReadonly();


  puede(permiso: Permiso): boolean {
    return this.permisos().includes(permiso);
  }

  iniciarSesion(credenciales: Credenciales) {
    this.errorSignal.set(null);
    this.cargandoSignal.set(true);

    this.http.post<RespuestaAuth>(`${API_URL}/auth/login`, credenciales).subscribe({
      next: (respuesta) => {
        this.tokenSignal.set(respuesta.token);
        localStorage.setItem(CLAVE_TOKEN, respuesta.token);
        this.cargandoSignal.set(false);
        this.router.navigateByUrl('/herramientas');
      },
      error: (error: HttpErrorResponse) => {
        this.cargandoSignal.set(false);
        this.errorSignal.set(mensajeDeError(error));
      }
    });
  }

  cerrarSesion() {
    this.tokenSignal.set(null);
    localStorage.removeItem(CLAVE_TOKEN);
    this.router.navigateByUrl('/login');
  }
}