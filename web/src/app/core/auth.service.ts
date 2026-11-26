import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';

import { mensajeDeError } from './errores';
import { Permiso, Rol } from './permisos';

const TOKEN_KEY = 'llave_token';

interface JwtPayload {
  sub?: string;
  nombre?: string;
  rol?: Rol;
  permisos?: Permiso[];
  exp?: number;
}

interface CredencialesLogin {
  email: string;
  password: string;
}

interface DatosRegistro {
  nombre: string;
  email: string;
  password: string;
}

interface RespuestaToken {
  token: string;
}

function decodificarPayloadUtf8(token: string): JwtPayload | null {
  try {
    const partes = token.split('.');
    if (partes.length !== 3) {
      return null;
    }

    const base64Url = partes[1].replace(/-/g, '+').replace(/_/g, '/');
    const pad = base64Url.length % 4;
    const base64 = pad ? base64Url + '='.repeat(4 - pad) : base64Url;

    const binario = atob(base64);
    const bytes = new Uint8Array(binario.length);
    for (let i = 0; i < binario.length; i++) {
      bytes[i] = binario.charCodeAt(i);
    }

    const json = new TextDecoder('utf-8').decode(bytes);
    return JSON.parse(json) as JwtPayload;
  } catch {
    return null;
  }
}

function tokenVigente(payload: JwtPayload | null): boolean {
  if (!payload || typeof payload.exp !== 'number') {
    return false;
  }
  const ahoraSegundos = Math.floor(Date.now() / 1000);
  return payload.exp > ahoraSegundos;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly token = signal<string | null>(this.leerTokenInicial());
  readonly error = signal<string | null>(null);
  readonly cargando = signal<boolean>(false);

  private readonly payload = computed<JwtPayload | null>(() => {
    const actual = this.token();
    if (!actual) {
      return null;
    }
    const parsed = decodificarPayloadUtf8(actual);
    return tokenVigente(parsed) ? parsed : null;
  });

  readonly sesionActiva = computed<boolean>(() => this.payload() !== null);
  readonly nombre = computed<string>(() => this.payload()?.nombre ?? '');
  readonly rol = computed<Rol | null>(() => this.payload()?.rol ?? null);
  readonly permisos = computed<Permiso[]>(() => this.payload()?.permisos ?? []);

  private leerTokenInicial(): string | null {
    try {
      return localStorage.getItem(TOKEN_KEY);
    } catch {
      return null;
    }
  }

  private guardarToken(nuevoToken: string | null): void {
    this.token.set(nuevoToken);
    try {
      if (nuevoToken) {
        localStorage.setItem(TOKEN_KEY, nuevoToken);
      } else {
        localStorage.removeItem(TOKEN_KEY);
      }
    } catch {
      // Ignorar errores de almacenamiento en entornos restringidos
    }
  }

  puede(permiso: Permiso): boolean {
    if (!this.sesionActiva()) {
      return false;
    }
    return this.permisos().includes(permiso);
  }

  iniciarSesion(credenciales: CredencialesLogin): void {
    this.cargando.set(true);
    this.error.set(null);

    this.http.post<RespuestaToken>('/api/auth/login', credenciales).subscribe({
      next: (res) => {
        this.guardarToken(res.token);
        this.cargando.set(false);
        void this.router.navigate(['/herramientas']);
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      }
    });
  }

  registrarse(datos: DatosRegistro) {
    this.cargando.set(true);
    this.error.set(null);

    return this.http.post<RespuestaToken>('/api/auth/registro', datos).pipe(
      tap({
        next: (res) => {
          this.guardarToken(res.token);
          this.cargando.set(false);
          void this.router.navigate(['/herramientas']);
        },
        error: (err) => {
          this.error.set(mensajeDeError(err));
          this.cargando.set(false);
        }
      })
    );
  }

  cerrarSesion(): void {
    this.guardarToken(null);
    this.error.set(null);
    void this.router.navigate(['/login']);
  }
}