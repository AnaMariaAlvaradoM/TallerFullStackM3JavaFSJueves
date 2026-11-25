import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';

export type Categoria = 'ELECTRICA' | 'CONSTRUCCION' | 'JARDIN' | 'MEDICION';



export interface Herramienta {
  id: number;
  nombre: string;
  categoria: Categoria;
  precioDia: number;
  deposito: number;
  unidades: number;
  descripcion: string | null;
  imagenUrl: string | null;
  activa: boolean;
}

export const NOMBRE_CATEGORIA: Record<Categoria, string> = {
  ELECTRICA: 'Eléctrica',
  CONSTRUCCION: 'Construcción',
  JARDIN: 'Jardín',
  MEDICION: 'Medición'
};

@Injectable({ providedIn: 'root' })
export class HerramientasService {
  private http = inject(HttpClient);
  private apiUrl = `${API_URL}/herramientas`;

  private lista = signal<Herramienta[]>([]);
  private cargandoSignal = signal(false);
  private errorSignal = signal<string | null>(null);

  herramientas = this.lista.asReadonly();
  cargando = this.cargandoSignal.asReadonly();
  error = this.errorSignal.asReadonly();

  // GET /api/herramientas: el backend decide si devuelve todos o solo los activos
  cargar() {
    this.cargandoSignal.set(true);
    this.errorSignal.set(null);

    this.http.get<Herramienta[]>(this.apiUrl).subscribe({
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

  // PATCH /api/herramientas/{id}/estado (exige HERRAMIENTAS_GESTIONAR)
  cambiarEstado(herramienta: Herramienta, activa: boolean) {
    this.errorSignal.set(null);

    this.http.patch<Herramienta>(`${this.apiUrl}/${herramienta.id}/estado`, { activa }).subscribe({
      next: (actualizado) => {
        this.lista.update(actuales =>
          actuales.map(e => (e.id === actualizado.id ? actualizado : e))
        );
      },
      error: (error: HttpErrorResponse) => this.errorSignal.set(mensajeDeError(error))
    });
  }
}