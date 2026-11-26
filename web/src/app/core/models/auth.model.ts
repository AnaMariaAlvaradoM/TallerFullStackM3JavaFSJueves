export interface AuthRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  email: string;
  nombre: string;
  rol: 'ADMIN' | 'CLIENTE';
  authorities: string[];
}
