import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';

export type Categoria = 'ELECTRICA' | 'CONSTRUCCION' | 'JARDIN' | 'MEDICION';

export interface Herramienta {
    id: number;
    nombre: string;
    categoria: Categoria;
    descripcion: string | null;
    precioDia: number;
    deposito: number;
    unidades: number;
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

    // GET /api/herramientas: el backend decide si devuelve todas o solo las activas
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
            next: (actualizada) => {
                this.lista.update(actuales =>
                    actuales.map(h => (h.id === actualizada.id ? actualizada : h))
                );
            },
            error: (error: HttpErrorResponse) => this.errorSignal.set(mensajeDeError(error))
        });
    }
}