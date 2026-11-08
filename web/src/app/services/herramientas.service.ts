import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';
import { Herramienta } from '../core/modelos';

// Habla con /api/herramientas y guarda el catálogo en señales
@Injectable({ providedIn: 'root' })
export class HerramientasService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/herramientas`;

  readonly herramientas = signal<Herramienta[]>([]);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.http.get<Herramienta[]>(this.url).subscribe({
      next: (lista) => {
        this.herramientas.set(lista);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.error.set(mensajeDeError(error));
        this.cargando.set(false);
      },
    });
  }

  // Devuelve el Observable: quien lo llama decide qué hacer con la respuesta
  obtener(id: number): Observable<Herramienta> {
    return this.http.get<Herramienta>(`${this.url}/${id}`);
  }

  // Retirar (activa = false) o volver a ofrecer (activa = true)
  cambiarEstado(herramienta: Herramienta, activa: boolean): void {
    this.error.set(null);
    this.http.patch<Herramienta>(`${this.url}/${herramienta.id}/estado`, { activa }).subscribe({
      // Reemplaza SOLO esa herramienta en la lista con lo que respondió el backend
      next: (actualizada) =>
        this.herramientas.update((lista) => lista.map((h) => (h.id === actualizada.id ? actualizada : h))),
      error: (error: HttpErrorResponse) => this.error.set(mensajeDeError(error)),
    });
  }
}
