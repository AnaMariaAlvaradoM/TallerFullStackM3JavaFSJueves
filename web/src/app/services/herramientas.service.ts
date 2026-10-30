import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable,inject, signal } from '@angular/core';
import { API_URL } from '../core/api';
import { mensajeDeError } from '../core/errores';

export type Categoria = 
    |'ELECTRICA'
    | 'CONSTRUCCION'
    | 'JARDIN'
    | 'MEDICION';


export interface Herramienta{
    id: number,
    nombre: string,
    categoria: Categoria,
    preciaDia: number,
    deposito: number,
    decripcion: string,
    imagenUrl: string,
    unidades: number,
    activa: boolean,

}

export interface DiasAlquilada{
    fechaInicio: string;
    fechaFin: string;
}

export const NOMBRE_CATEGORIA: Record<Categoria, string> ={
    ELECTRICA   : 'Eléctrica',
    CONSTRUCCION: 'Construcción',
    JARDIN: 'Jardín',
    MEDICION: 'Medición'
};


@Injectable({providedIn: 'root'})
export class HerramientasService {
    private http = inject(HttpClient);
    private apiUrl = `${API_URL}/herramientas`;

    private lista = signal<Herramienta[]>([]);
    private cargandoSignal = signal(false);
    private errorsignal = signal<string|null>(null);

    herramientas = this.lista.asReadonly();
    cargando = this.cargandoSignal.asReadonly();
    error = this.errorsignal.asReadonly();

    //Lo usa el backend para saber si devuelve todas las herramientas o solo las activos
    cargar(){
        this.cargandoSignal.set(true);
        this.errorsignal.set(null);

        this.http.get<Herramienta[]>(this.apiUrl).subscribe({
            next: (datos) =>{
                this.lista.set(datos);
                this.cargandoSignal.set(false)
            },
            error: (error: HttpErrorResponse) =>{
                this.errorsignal.set(mensajeDeError(error));
                this.cargandoSignal.set(false);
            }
        });

    }

    cambiarEstado(herramienta: Herramienta, activa:boolean){
        this.errorsignal.set(null);

        this.http.patch<Herramienta>(`${this.apiUrl}/${herramienta.id}/estado`, {activa}).subscribe({
            next: (actualizado) =>{
                this.lista.update(actuales =>
                    actuales.map(e => e.id === actualizado.id ? actualizado: e)
                );
            },
            error: (error: HttpErrorResponse) => this.errorsignal.set(mensajeDeError(error))
        });
    }
}
