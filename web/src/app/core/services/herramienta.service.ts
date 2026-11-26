import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EstadoHerramientaRequest, HerramientaResponse } from '../models/herramienta.model';

@Injectable({
  providedIn: 'root',
})
export class HerramientaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/herramientas';

  listarHerramientas(): Observable<HerramientaResponse[]> {
    return this.http.get<HerramientaResponse[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<HerramientaResponse> {
    return this.http.get<HerramientaResponse>(`${this.apiUrl}/${id}`);
  }

  cambiarEstado(id: number, request: EstadoHerramientaRequest): Observable<HerramientaResponse> {
    return this.http.patch<HerramientaResponse>(`${this.apiUrl}/${id}/estado`, request);
  }
}
