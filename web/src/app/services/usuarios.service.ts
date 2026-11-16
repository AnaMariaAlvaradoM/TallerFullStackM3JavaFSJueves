import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
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
  private apiUrl = `${API_URL}/usuarios`;

  private lista = signal<Usuario[]>([]);
  private cargandoSignal = signal(false);
  private errorSignal = signal<string | null>(null);

  usuarios = this.lista.asReadonly();
  cargando = this.cargandoSignal.asReadonly();
  error = this.errorSignal.asReadonly();

  // GET /api/usuarios (exige CLIENTES_GESTIONAR)
  cargar() {
    this.cargandoSignal.set(true);
    this.errorSignal.set(null);

    this.http.get<Usuario[]>(this.apiUrl).subscribe({
      next: (datos) => {
        this.lista.set(datos);
        this.cargandoSignal.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.errorSignal.set(mensajeDeError(error));
        this.cargandoSignal.set(false);
      }
    });
  }

  // PATCH /api/usuarios/{id}/estado
  cambiarEstado(usuario: Usuario, activo: boolean) {
    this.errorSignal.set(null);

    this.http.patch<Usuario>(`${this.apiUrl}/${usuario.id}/estado`, { activo }).subscribe({
      next: (actualizado) => {
        this.lista.update(actuales =>
          actuales.map(u => (u.id === actualizado.id ? actualizado : u))
        );
      },
      error: (error: HttpErrorResponse) => this.errorSignal.set(mensajeDeError(error))
    });
  }
}