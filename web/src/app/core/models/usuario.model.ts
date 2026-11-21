export type Rol = 'ADMIN' | 'CLIENTE';

export interface UsuarioResponse {
  id: number;
  nombre: string;
  email: string;
  activo: boolean;
  creadoEn: string;
  username: string;
  rol: Rol;
}

export interface EstadoUsuarioRequest {
  activo: boolean;
}
