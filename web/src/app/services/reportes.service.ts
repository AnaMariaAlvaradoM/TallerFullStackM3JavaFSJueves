import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../core/api';
import { Alquiler } from './alquileres.service';

export interface Resumen {
  ingresosMes: number;
  alquileresActivos: number;
  vencidos: Alquiler[];
  masAlquiladas: { herramienta: string; alquileres: number }[];
}

@Injectable({ providedIn: 'root' })
export class ReportesService {
  private http = inject(HttpClient);

  resumen(): Observable<Resumen> {
    return this.http.get<Resumen>(`${API_URL}/reportes/resumen`);
  }
}
