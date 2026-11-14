import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { API_URL } from '../core/api';
import { Observable } from 'rxjs';
import { mensajeDeError } from '../core/errores';

export type EstadoAlquiler = 'RESERVADO' | 'CANCELADA';

export interface Alquiler {
    id: number;
    herramientaId: number;
    herramientaNombre: string;
    clienteId: number;
    clienteNombre: string;
    fechaInicio: string;
    fechaFin: string;
    dias: number;
    deposito: number;
    estado: EstadoAlquiler;
    creadoEn: string;
    valorTotal: number;

}

export interface NuevoAlquiler{
    herramientaId: number
    fechaInicio: string,
    fechaFin: string
}

export function sePuedeCancelar(alquiler:Alquiler, ahora: Date): boolean{
    return alquiler.estado === 'RESERVADO' && new Date(`${alquiler.fechaInicio}`)> ahora;
}

@Injectable({providedIn: 'root'})
export class AlquileresService {

    private http = inject(HttpClient);
    private apiUrl = `${API_URL}/alquileres`;

    private lista = signal<Alquiler[]>([]);
    private cargandoSignal = signal(false);
    private errorSignal = signal<string | null> (null);

    alquileres = this.lista.asReadonly();
    cargando = this.cargandoSignal.asReadonly();
    error = this.errorSignal.asReadonly();

    crear(datos: NuevoAlquiler): Observable<Alquiler>{
        return this.http.post<Alquiler>(this.apiUrl,datos);
    }

    cargarMios(){
        this.cargandoSignal.set(true);
        this.errorSignal.set(null);

        this.http.get<Alquiler[]>(`${this.apiUrl}/mios`).subscribe({
            next: (datos) => {
                this.lista.set(datos);
                this.cargandoSignal.set(false);
            },
            error: (error: HttpErrorResponse)=> {
                this.errorSignal.set(mensajeDeError(error));
                this.cargandoSignal.set(false);
            }
        })
    }

    cancelar(alquiler: Alquiler){
        this.errorSignal.set(null);

        this.http.patch<Alquiler>(`${this.apiUrl}/${alquiler.id}/cancelar`,{}).subscribe({
            next: (actualizada) => {
                this.lista.update(actuales =>
                    actuales.map(r => (r.id === actualizada.id ? actualizada : r))
                );
            },
            error: (error: HttpErrorResponse) => this.errorSignal.set(mensajeDeError(error))
        });
    }


}
