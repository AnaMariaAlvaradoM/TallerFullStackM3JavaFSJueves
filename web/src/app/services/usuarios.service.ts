import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';

import { mensajeDeError } from '../core/errores';
import { Rol } from '../core/permisos';

export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  rol: Rol;
  activo: boolean;
  creadoEn: string;
}

@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private readonly http = inject(HttpClient);

  readonly usuarios = signal<Usuario[]>([]);
  readonly cargando = signal<boolean>(false);
  readonly error = signal<string | null>(null);

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);

    this.http.get<Usuario[]>('/api/usuarios').subscribe({
      next: (lista) => {
        this.usuarios.set(lista);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      }
    });
  }

  cambiarEstado(usuario: Usuario, activo: boolean): void {
    this.error.set(null);

    this.http
      .patch<Usuario>(`/api/usuarios/${usuario.id}/estado`, { activo })
      .subscribe({
        next: (actualizado) => {
          this.usuarios.update((actual) =>
            actual.map((item) => (item.id === actualizado.id ? actualizado : item))
          );
        },
        error: (err) => {
          this.error.set(mensajeDeError(err));
        }
      });
  }
}