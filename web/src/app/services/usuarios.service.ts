import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import {Rol} from '../core/permisos';

export interface Usuario {
    id: number;
    nombre: string;
    email: string;
    password: string;
    rol: Rol;
    activa: boolean;
    creadoEn: string;
}


@Injectable({providedIn: 'root'})
export class UsuariosService {
    private http = inject(HttpClient);
    private apiUrl = '${API_URL}/usuarios';

    private lista = signal<Usuario[]>([]);
    private cargandoSignal = signal(false);
    private errorSignal = signal<string | null>(null);

    usuarios = this.lista.asReadonly();
    cargando = this.cargandoSignal.asReadonly();
    error = this.errorSignal.asReadonly();

    cargar(){
        this.cargandoSignal.set(true);
        this.errorSignal.set(null);

        this.http.get<Usuario[]>(this.apiUrl).subscribe({
            next: (datos) =>{
                this.lista.set(datos);
                this.cargandoSignal.set(false);
            },
            error: () => {
                this.errorSignal.set(
                    'No se pudo establecer conexión con el servidor de LlaveMaestra. Verifica que el backend este corriendo en el puerto 8080.'
                );
                this.cargandoSignal.set(false);
            }
        });
    }
    

   
}
