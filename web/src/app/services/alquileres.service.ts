import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable } from 'rxjs';

import { mensajeDeError } from '../core/errores';

export type EstadoAlquiler = 'RESERVADO' | 'ENTREGADO' | 'DEVUELTO' | 'CANCELADO';

export interface Alquiler {
  id: number;
  herramientaId: number;
  herramientaNombre: string;
  clienteId: number;
  clienteNombre: string;
  fechaInicio: string;
  fechaFin: string;
  dias: number;
  valorTotal: number;
  deposito: number;
  estado: EstadoAlquiler;
  creadoEn: string;
}

export interface CrearAlquilerRequest {
  herramientaId: number;
  fechaInicio: string;
  fechaFin: string;
}

@Injectable({ providedIn: 'root' })
export class AlquileresService {
  private readonly http = inject(HttpClient);

  readonly alquileres = signal<Alquiler[]>([]);
  readonly cargando = signal<boolean>(false);
  readonly error = signal<string | null>(null);

  crear(datos: CrearAlquilerRequest): Observable<Alquiler> {
    this.error.set(null);
    return this.http.post<Alquiler>('/api/alquileres', datos);
  }

  cargarMios(): void {
    this.cargando.set(true);
    this.error.set(null);

    this.http.get<Alquiler[]>('/api/alquileres/mios').subscribe({
      next: (lista) => {
        this.alquileres.set(lista);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      }
    });
  }

  cargarTodos(estado?: string): void {
    this.cargando.set(true);
    this.error.set(null);

    let params = new HttpParams();
    if (estado && estado.trim() !== '') {
      params = params.set('estado', estado.trim());
    }

    this.http.get<Alquiler[]>('/api/alquileres', { params }).subscribe({
      next: (lista) => {
        this.alquileres.set(lista);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      }
    });
  }

  cancelar(alquiler: Alquiler): void {
    this.error.set(null);

    this.http.patch<Alquiler>(`/api/alquileres/${alquiler.id}/cancelar`, {}).subscribe({
      next: (actualizado) => {
        this.alquileres.update((actual) =>
          actual.map((item) => (item.id === actualizado.id ? actualizado : item))
        );
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
      }
    });
  }

  entregar(alquiler: Alquiler): void {
    this.error.set(null);

    this.http.patch<Alquiler>(`/api/alquileres/${alquiler.id}/entregar`, {}).subscribe({
      next: (actualizado) => {
        this.alquileres.update((actual) =>
          actual.map((item) => (item.id === actualizado.id ? actualizado : item))
        );
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
      }
    });
  }

  devolver(alquiler: Alquiler): void {
    this.error.set(null);

    this.http.patch<Alquiler>(`/api/alquileres/${alquiler.id}/devolver`, {}).subscribe({
      next: (actualizado) => {
        this.alquileres.update((actual) =>
          actual.map((item) => (item.id === actualizado.id ? actualizado : item))
        );
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
      }
    });
  }
}