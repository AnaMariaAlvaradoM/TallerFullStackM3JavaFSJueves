import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';
import { Usuario } from '../core/modelos';

// Habla con /api/usuarios (solo para quien tiene CLIENTES_GESTIONAR)
@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/usuarios`;

  readonly usuarios = signal<Usuario[]>([]);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.http.get<Usuario[]>(this.url).subscribe({
      next: (lista) => {
        this.usuarios.set(lista);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.error.set(mensajeDeError(error));
        this.cargando.set(false);
      },
    });
  }

  // Bloquear (activo = false) o desbloquear (activo = true)
  cambiarEstado(usuario: Usuario, activo: boolean): void {
    this.error.set(null);
    this.http.patch<Usuario>(`${this.url}/${usuario.id}/estado`, { activo }).subscribe({
      next: (actualizado) =>
        this.usuarios.update((lista) => lista.map((u) => (u.id === actualizado.id ? actualizado : u))),
      error: (error: HttpErrorResponse) => this.error.set(mensajeDeError(error)),
    });
  }
}
