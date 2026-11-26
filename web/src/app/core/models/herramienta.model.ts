export interface HerramientaResponse {
  id: number;
  nombre: string;
  descripcion: string;
  precioDia: number;
  activa: boolean;
  categoria: string;
}

export interface EstadoHerramientaRequest {
  activa: boolean;
}
