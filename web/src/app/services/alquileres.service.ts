import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';

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
  estado: 'RESERVADO' | 'ENTREGADO' | 'DEVUELTO' | 'CANCELADO';
  creadoEn: string;
}

@Injectable({ providedIn: 'root' })
export class AlquileresService {
  private http = inject(HttpClient);

  alquileres = signal<Alquiler[]>([]);
  cargando = signal<boolean>(false);
  error = signal<string | null>(null);

  crear(datos: { herramientaId: number; fechaInicio: string; fechaFin: string }): Observable<Alquiler> {
    return this.http.post<Alquiler>(`${API_URL}/alquileres`, datos);
  }

  cargarMios(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.http.get<Alquiler[]>(`${API_URL}/alquileres/mios`).subscribe({
      next: (lista) => {
        this.alquileres.set(lista);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      },
    });
  }

  cargarTodos(estado?: string): void {
    this.cargando.set(true);
    this.error.set(null);
    const url = estado ? `${API_URL}/alquileres?estado=${estado}` : `${API_URL}/alquileres`;
    this.http.get<Alquiler[]>(url).subscribe({
      next: (lista) => {
        this.alquileres.set(lista);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      },
    });
  }

  cancelar(alquiler: Alquiler): void {
    this.http.patch<Alquiler>(`${API_URL}/alquileres/${alquiler.id}/cancelar`, {}).subscribe({
      next: (actualizado) => {
        this.alquileres.update((lista) =>
          lista.map((a) => (a.id === actualizado.id ? actualizado : a))
        );
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
      },
    });
  }

  entregar(alquiler: Alquiler): void {
    this.http.patch<Alquiler>(`${API_URL}/alquileres/${alquiler.id}/entregar`, {}).subscribe({
      next: (actualizado) => {
        this.alquileres.update((lista) =>
          lista.map((a) => (a.id === actualizado.id ? actualizado : a))
        );
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
      },
    });
  }

  devolver(alquiler: Alquiler): void {
    this.http.patch<Alquiler>(`${API_URL}/alquileres/${alquiler.id}/devolver`, {}).subscribe({
      next: (actualizado) => {
        this.alquileres.update((lista) =>
          lista.map((a) => (a.id === actualizado.id ? actualizado : a))
        );
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
      },
    });
  }
}
