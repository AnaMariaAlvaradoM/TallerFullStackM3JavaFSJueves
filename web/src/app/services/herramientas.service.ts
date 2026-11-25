import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { mensajeDeError } from '../core/errores';

export type CategoriaHerramienta = 'ELECTRICA' | 'CONSTRUCCION' | 'JARDIN' | 'MEDICION';

export interface Herramienta {
  id: number;
  nombre: string;
  categoria: CategoriaHerramienta;
  descripcion: string | null;
  precioDia: number;
  deposito: number;
  unidades: number;
  imagenUrl: string | null;
  activa: boolean;
}

export interface DatosHerramienta {
  nombre: string;
  categoria: CategoriaHerramienta;
  descripcion?: string | null;
  precioDia: number;
  deposito: number;
  unidades: number;
  imagenUrl?: string | null;
}

@Injectable({ providedIn: 'root' })
export class HerramientasService {
  private readonly http = inject(HttpClient);

  readonly herramientas = signal<Herramienta[]>([]);
  readonly cargando = signal<boolean>(false);
  readonly error = signal<string | null>(null);

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);

    this.http.get<Herramienta[]>('/api/herramientas').subscribe({
      next: (lista) => {
        this.herramientas.set(lista);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      }
    });
  }

  obtener(id: number): Observable<Herramienta> {
    return this.http.get<Herramienta>(`/api/herramientas/${id}`);
  }

  crear(datos: DatosHerramienta): Observable<Herramienta> {
    this.error.set(null);
    return this.http.post<Herramienta>('/api/herramientas', datos).pipe(
      tap({
        next: (creada) => {
          this.herramientas.update((actual) => [...actual, creada]);
        },
        error: (err) => {
          this.error.set(mensajeDeError(err));
        }
      })
    );
  }

  actualizar(id: number, datos: DatosHerramienta): Observable<Herramienta> {
    this.error.set(null);
    return this.http.put<Herramienta>(`/api/herramientas/${id}`, datos).pipe(
      tap({
        next: (actualizada) => {
          this.herramientas.update((actual) =>
            actual.map((item) => (item.id === actualizada.id ? actualizada : item))
          );
        },
        error: (err) => {
          this.error.set(mensajeDeError(err));
        }
      })
    );
  }

  cambiarEstado(herramienta: Herramienta, activa: boolean): void {
    this.error.set(null);

    this.http
      .patch<Herramienta>(`/api/herramientas/${herramienta.id}/estado`, { activa })
      .subscribe({
        next: (actualizada) => {
          this.herramientas.update((actual) =>
            actual.map((item) => (item.id === actualizada.id ? actualizada : item))
          );
        },
        error: (err) => {
          this.error.set(mensajeDeError(err));
        }
      });
  }
}