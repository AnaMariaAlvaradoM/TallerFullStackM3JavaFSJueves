import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { Alquiler } from './alquileres.service';

export interface HerramientaRanking {
  herramienta: string;
  alquileres: number;
}

export interface Resumen {
  ingresosMes: number;
  alquileresActivos: number;
  vencidos: Alquiler[];
  masAlquiladas: HerramientaRanking[];
}

@Injectable({ providedIn: 'root' })
export class ReportesService {
  private readonly http = inject(HttpClient);

  resumen(): Observable<Resumen> {
    return this.http.get<Resumen>('/api/reportes/resumen');
  }
}