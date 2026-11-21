import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { API_URL } from './api';
import { mensajeDeError } from './errores';
import { Permiso, Rol } from './permisos';

export const CLAVE_TOKEN = 'llave_token';

interface TokenPayload {
  sub?: string;
  nombre?: string;
  rol?: Rol;
  permisos?: Permiso[];
  exp?: number;
}

function decodificarToken(token: string): TokenPayload | null {
  try {
    const parte = token.split('.')[1];
    if (!parte) return null;
    const base64 = parte.replace(/-/g, '+').replace(/_/g, '/');
    const binario = atob(base64);
    const bytes = Uint8Array.from(binario, (c) => c.charCodeAt(0));
    const json = new TextDecoder('utf-8').decode(bytes);
    return JSON.parse(json);
  } catch {
    return null;
  }
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  token = signal<string | null>(localStorage.getItem(CLAVE_TOKEN));
  cargando = signal<boolean>(false);
  error = signal<string | null>(null);

  private payload = computed(() => {
    const t = this.token();
    return t ? decodificarToken(t) : null;
  });

  sesionActiva = computed(() => {
    const p = this.payload();
    if (!p || !p.exp) return false;
    return p.exp * 1000 > Date.now();
  });

  nombre = computed(() => this.payload()?.nombre ?? '');
  rol = computed(() => this.payload()?.rol ?? null);
  permisos = computed(() => (this.sesionActiva() ? (this.payload()?.permisos ?? []) : []));

  puede(permiso: Permiso | string): boolean {
    if (!this.sesionActiva()) return false;
    return this.permisos().includes(permiso as Permiso);
  }

  iniciarSesion(credenciales: { email?: string; password?: string }): void {
    this.cargando.set(true);
    this.error.set(null);

    this.http.post<{ token: string }>(`${API_URL}/auth/login`, credenciales).subscribe({
      next: (res) => {
        this.cargando.set(false);
        this.establecerToken(res.token);
        this.router.navigateByUrl('/herramientas');
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(mensajeDeError(err));
      },
    });
  }

  registrarse(datos: { nombre: string; email: string; password: string }) {
    this.cargando.set(true);
    this.error.set(null);

    const obs = this.http.post<{ token: string }>(`${API_URL}/auth/registro`, datos);
    obs.subscribe({
      next: (res) => {
        this.cargando.set(false);
        this.establecerToken(res.token);
        this.router.navigateByUrl('/herramientas');
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(mensajeDeError(err));
      },
    });
    return obs;
  }

  cerrarSesion(): void {
    localStorage.removeItem(CLAVE_TOKEN);
    this.token.set(null);
    this.error.set(null);
    this.router.navigateByUrl('/login');
  }

  establecerToken(token: string): void {
    localStorage.setItem(CLAVE_TOKEN, token);
    this.token.set(token);
  }
}
