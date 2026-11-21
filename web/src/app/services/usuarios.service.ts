import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { API_URL } from '../core/api';
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
  private http = inject(HttpClient);

  usuarios = signal<Usuario[]>([]);
  cargando = signal<boolean>(false);
  error = signal<string | null>(null);

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.http.get<Usuario[]>(`${API_URL}/usuarios`).subscribe({
      next: (lista) => {
        this.usuarios.set(lista);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      },
    });
  }

  cambiarEstado(usuario: Usuario, activo: boolean): void {
    this.http
      .patch<Usuario>(`${API_URL}/usuarios/${usuario.id}/estado`, { activo })
      .subscribe({
        next: (actualizado) => {
          this.usuarios.update((lista) =>
            lista.map((u) => (u.id === actualizado.id ? actualizado : u))
          );
        },
        error: (err) => {
          this.error.set(mensajeDeError(err));
        },
      });
  }
}
