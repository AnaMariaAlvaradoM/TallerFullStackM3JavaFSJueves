import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';

export interface Herramienta {
  id: number;
  nombre: string;
  categoria: string;
  descripcion?: string | null;
  precioDia: number;
  deposito: number;
  unidades: number;
  imagenUrl?: string | null;
  activa: boolean;
}

@Injectable({ providedIn: 'root' })
export class HerramientasService {
  private http = inject(HttpClient);

  herramientas = signal<Herramienta[]>([]);
  cargando = signal<boolean>(false);
  error = signal<string | null>(null);

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.http.get<Herramienta[]>(`${API_URL}/herramientas`).subscribe({
      next: (lista) => {
        this.herramientas.set(lista);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      },
    });
  }

  buscarPorId(id: number | string): Observable<Herramienta> {
    return this.http.get<Herramienta>(`${API_URL}/herramientas/${id}`);
  }

  cambiarEstado(herramienta: Herramienta, activa: boolean): void {
    this.http
      .patch<Herramienta>(`${API_URL}/herramientas/${herramienta.id}/estado`, { activa })
      .subscribe({
        next: (actualizada) => {
          this.herramientas.update((lista) =>
            lista.map((h) => (h.id === actualizada.id ? actualizada : h))
          );
        },
        error: (err) => {
          this.error.set(mensajeDeError(err));
        },
      });
  }

  crear(datos: Partial<Herramienta>): Observable<Herramienta> {
    return this.http.post<Herramienta>(`${API_URL}/herramientas`, datos);
  }

  actualizar(id: number | string, datos: Partial<Herramienta>): Observable<Herramienta> {
    return this.http.put<Herramienta>(`${API_URL}/herramientas/${id}`, datos);
  }
}
