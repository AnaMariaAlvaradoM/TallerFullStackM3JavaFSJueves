import { Service, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';

export type TipoCategoria = 'ELECTRICA' | 'CONSTRUCCION' | 'JARDIN' | 'MEDICION';

export interface Herramienta {
  id: number;
  nombre: string;
  categoria: TipoCategoria;
  deposito: number;
  precioDia: number;
  descripcion?: string | null;
  unidades: number;
  imagenUrl?: string | null;
  activa: boolean;
}

export interface NuevaHerramienta {
  nombre: string;
  categoria: TipoCategoria;
  deposito: number;
  precioDia: number;
  descripcion?: string | null;
  unidades: number;
  imagenUrl?: string | null;
}

export interface FranjaOcupada {
  fechaInicio: string;
  fechaFin: string;
}

export const NOMBRE_CATEGORIA: Record<TipoCategoria, string> = {
  ELECTRICA: 'Eléctrica',
  CONSTRUCCION: 'Construcción',
  JARDIN: 'Jardín',
  MEDICION: 'Medición',
};

@Service()
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
      },
    });
  }

  // GET /api/herramientas/{id}: lo usa la página Reservar
  obtener(id: number): Observable<Herramienta> {
    return this.http.get<Herramienta>(`${this.apiUrl}/${id}`);
  }

  // GET /api/herramientas/{id}/ocupacion?fecha=AAAA-MM-DD: los horarios ya tomados ese día
  ocupacion(id: number, fecha: string): Observable<FranjaOcupada[]> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<FranjaOcupada[]>(`${this.apiUrl}/${id}/ocupacion`, { params });
  }

  // POST /api/herramientas (exige _GESTIONAR)
  crear(datos: NuevaHerramienta): Observable<Herramienta> {
    return this.http.post<Herramienta>(this.apiUrl, datos);
  }

  // PUT /api/herramientas/{id} (exige HERRAMIENTAS_GESTIONAR)
  actualizar(id: number, datos: NuevaHerramienta): Observable<Herramienta> {
    return this.http.put<Herramienta>(`${this.apiUrl}/${id}`, datos);
  }

  cambiarEstado(herramienta: Herramienta, activa: boolean) {
    this.errorSignal.set(null);

    this.http.patch<Herramienta>(`${this.apiUrl}/${herramienta.id}/estado`, { activa }).subscribe({
      next: (actualizado) => {
        this.lista.update((actuales) =>
          actuales.map((e) => (e.id === actualizado.id ? actualizado : e)),
        );
      },
      error: (error: HttpErrorResponse) => this.errorSignal.set(mensajeDeError(error)),
    });
  }
}
