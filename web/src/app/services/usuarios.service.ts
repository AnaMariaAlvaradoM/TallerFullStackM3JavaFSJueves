import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';
import { Rol } from '../core/permisos';

export interface UsuarioDto {
  id: number;
  nombre: string;
  email: string;
  rol: Rol;
  activo: boolean;
  creadoEn: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsuariosService {
  private http = inject(HttpClient);

  private usuariosSignal = signal<UsuarioDto[]>([]);
  private cargandoSignal = signal(false);
  private errorSignal = signal<string | null>(null);

  usuarios = this.usuariosSignal.asReadonly();
  cargando = this.cargandoSignal.asReadonly();
  error = this.errorSignal.asReadonly();

  cargar() {
    this.cargandoSignal.set(true);
    this.errorSignal.set(null);

    this.http.get<UsuarioDto[]>(`${API_URL}/usuarios`).subscribe({
      next: (data) => {
        this.usuariosSignal.set(data);
        this.cargandoSignal.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.errorSignal.set(mensajeDeError(err));
        this.cargandoSignal.set(false);
      }
    });
  }

  cambiarEstado(usuario: UsuarioDto, activo: boolean) {
    this.errorSignal.set(null);

    this.http.patch<UsuarioDto>(`${API_URL}/usuarios/${usuario.id}/estado`, { activo }).subscribe({
      next: (actualizado) => {
        this.usuariosSignal.update(lista =>
          lista.map(item => (item.id === actualizado.id ? actualizado : item))
        );
      },
      error: (err: HttpErrorResponse) => {
        this.errorSignal.set(mensajeDeError(err));
      }
    });
  }
}