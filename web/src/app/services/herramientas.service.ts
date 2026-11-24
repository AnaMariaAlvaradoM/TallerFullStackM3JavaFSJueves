import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';

export type Categoria = 'ELECTRICA' | 'CONSTRUCCION' | 'JARDIN' | 'MEDICION';

export interface HerramientaDto {
  id: number;
  nombre: string;
  categoria: Categoria;
  descripcion: string;
  precioDia: number;
  deposito: number;
  unidades: number;
  imagenUrl: string;
  activa: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class HerramientasService {
  private http = inject(HttpClient);

  private herramientasSignal = signal<HerramientaDto[]>([]);
  private cargandoSignal = signal(false);
  private errorSignal = signal<string | null>(null);

  herramientas = this.herramientasSignal.asReadonly();
  cargando = this.cargandoSignal.asReadonly();
  error = this.errorSignal.asReadonly();

  cargar() {
    this.cargandoSignal.set(true);
    this.errorSignal.set(null);

    this.http.get<HerramientaDto[]>(`${API_URL}/herramientas`).subscribe({
      next: (data) => {
        this.herramientasSignal.set(data);
        this.cargandoSignal.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.errorSignal.set(mensajeDeError(err));
        this.cargandoSignal.set(false);
      }
    });
  }

  cambiarEstado(herramienta: HerramientaDto, activa: boolean) {
    this.errorSignal.set(null);

    this.http.patch<HerramientaDto>(`${API_URL}/herramientas/${herramienta.id}/estado`, { activa }).subscribe({
      next: (actualizada) => {
        this.herramientasSignal.update(lista =>
          lista.map(item => (item.id === actualizada.id ? actualizada : item))
        );
      },
      error: (err: HttpErrorResponse) => {
        this.errorSignal.set(mensajeDeError(err));
      }
    });
  }
}