import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { API_URL } from './api';
import { mensajeDeError } from './errores';
import { Permiso, Rol } from './permisos';

const CLAVE_TOKEN = 'llave_token';

// Lo que trae el payload (la parte del medio) del JWT
interface DatosToken {
  sub: string;
  nombre: string;
  rol: Rol;
  permisos: Permiso[];
  exp: number; // segundos desde 1970
}

// Lee el payload del token. Se decodifica como UTF-8 para que "Hernán" no salga como "HernÃ¡n".
function leerToken(token: string | null): DatosToken | null {
  if (!token) return null;
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const binario = atob(base64);
    const bytes = Uint8Array.from(binario, (letra) => letra.charCodeAt(0));
    return JSON.parse(new TextDecoder('utf-8').decode(bytes)) as DatosToken;
  } catch {
    return null; // token dañado: como si no hubiera sesión
  }
}

// La sesión de la aplicación: guarda el token y expone sus datos como señales
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  // El token se lee de localStorage al crearse el servicio (así la sesión sobrevive a un F5)
  readonly token = signal<string | null>(localStorage.getItem(CLAVE_TOKEN));
  readonly error = signal<string | null>(null);
  readonly cargando = signal(false);

  private readonly datos = computed(() => leerToken(this.token()));

  // Hay sesión si hay token y su fecha de vencimiento (exp) todavía no llega
  readonly sesionActiva = computed(() => {
    const datos = this.datos();
    return datos !== null && datos.exp * 1000 > Date.now();
  });

  readonly nombre = computed(() => (this.sesionActiva() ? this.datos()!.nombre : ''));
  readonly email = computed(() => (this.sesionActiva() ? this.datos()!.sub : ''));
  readonly rol = computed<Rol | null>(() => (this.sesionActiva() ? this.datos()!.rol : null));
  readonly permisos = computed<Permiso[]>(() => (this.sesionActiva() ? (this.datos()!.permisos ?? []) : []));

  puede(permiso: Permiso): boolean {
    return this.permisos().includes(permiso);
  }

  iniciarSesion(credenciales: { email: string; password: string }): void {
    this.cargando.set(true);
    this.error.set(null);

    this.http.post<{ token: string }>(`${API_URL}/auth/login`, credenciales).subscribe({
      next: (respuesta) => {
        localStorage.setItem(CLAVE_TOKEN, respuesta.token);
        this.token.set(respuesta.token);
        this.cargando.set(false);
        this.router.navigateByUrl('/herramientas');
      },
      error: (error: HttpErrorResponse) => {
        this.error.set(mensajeDeError(error));
        this.cargando.set(false);
      },
    });
  }

  cerrarSesion(): void {
    localStorage.removeItem(CLAVE_TOKEN);
    this.token.set(null);
    this.router.navigateByUrl('/login');
  }
}
