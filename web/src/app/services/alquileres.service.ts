import { Service, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';

export type EstadoAlquiler = 'RESERVADO' | 'ENTREGADO' | 'DEVUELTO' | 'CANCELADO';

export interface Alquiler {
  id: number;
  herramientaId: number;
  herramientaNombre: string;
  clienteId: number;
  clienteNombre: string;
  fechaInicio: string; // 'AAAA-MM-DD'
  fechaFin: string; // 'AAAA-MM-DD'
  dias: number;
  valorTotal: number;
  deposito: number;
  estado: EstadoAlquiler;
  creadoEn: string; // 'AAAA-MM-DDTHH:mm:ss'
}

export interface NuevoAlquiler {
  herramientaId: number;
  fechaInicio: string;
  fechaFin: string;
}

@Service()
export class AlquileresService {
  private http = inject(HttpClient);
  private apiUrl = `${API_URL}/alquileres`;

  private lista = signal<Alquiler[]>([]);
  private cargandoSignal = signal(false);
  private errorSignal = signal<string | null>(null);

  alquileres = this.lista.asReadonly();
  cargando = this.cargandoSignal.asReadonly();
  error = this.errorSignal.asReadonly();

  // POST /api/alquileres: devuelve el Observable para que la página decida qué hacer
  crear(datos: NuevoAlquiler): Observable<Alquiler> {
    return this.http.post<Alquiler>(this.apiUrl, datos);
  }

  // GET /api/alquileres/mios: llena alquileres()
  cargarMios() {
    this.cargandoSignal.set(true);
    this.errorSignal.set(null);

    this.http.get<Alquiler[]>(`${this.apiUrl}/mios`).subscribe({
      next: (datos) => {
        this.lista.set(datos);
        this.cargandoSignal.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.errorSignal.set(mensajeDeError(error));
        this.cargandoSignal.set(false);
      },
    });
  }

  // PATCH /api/alquileres/{id}/cancelar: reemplaza ese alquiler en la lista
  cancelar(alquiler: Alquiler) {
    this.errorSignal.set(null);

    this.http.patch<Alquiler>(`${this.apiUrl}/${alquiler.id}/cancelar`, {}).subscribe({
      next: (actualizado) => {
        this.lista.update((actuales) =>
          actuales.map((a) => (a.id === actualizado.id ? actualizado : a)),
        );
      },
      error: (error: HttpErrorResponse) => this.errorSignal.set(mensajeDeError(error)),
    });
  }
}