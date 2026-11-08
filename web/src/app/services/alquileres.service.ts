import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';
import { Alquiler, NuevoAlquiler } from '../core/modelos';

// Habla con /api/alquileres
@Injectable({ providedIn: 'root' })
export class AlquileresService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/alquileres`;

  readonly alquileres = signal<Alquiler[]>([]);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  // Solo se envían herramienta y fechas: el valor y el cliente los decide el backend.
  // Devuelve el Observable para que la página decida qué hacer (navegar o mostrar el error).
  crear(datos: NuevoAlquiler): Observable<Alquiler> {
    const cuerpo: NuevoAlquiler = {
      herramientaId: datos.herramientaId,
      fechaInicio: datos.fechaInicio,
      fechaFin: datos.fechaFin,
    };
    return this.http.post<Alquiler>(this.url, cuerpo);
  }

  cargarMios(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.http.get<Alquiler[]>(`${this.url}/mios`).subscribe({
      next: (lista) => {
        this.alquileres.set(lista);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.error.set(mensajeDeError(error));
        this.cargando.set(false);
      },
    });
  }

  cancelar(alquiler: Alquiler): void {
    this.error.set(null);
    this.http.patch<Alquiler>(`${this.url}/${alquiler.id}/cancelar`, {}).subscribe({
      next: (actualizado) =>
        this.alquileres.update((lista) => lista.map((a) => (a.id === actualizado.id ? actualizado : a))),
      error: (error: HttpErrorResponse) => this.error.set(mensajeDeError(error)),
    });
  }
}
