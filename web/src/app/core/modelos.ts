import { Rol } from './permisos';

// La forma de los datos que responde el backend (los DTO de Java)

export type Categoria = 'ELECTRICA' | 'CONSTRUCCION' | 'JARDIN' | 'MEDICION';

export interface Herramienta {
  id: number;
  nombre: string;
  categoria: Categoria;
  descripcion: string;
  precioDia: number;
  deposito: number;
  unidades: number;
  imagenUrl: string | null;
  activa: boolean;
}

export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  rol: Rol;
  activo: boolean;
  creadoEn: string;
}

export type EstadoAlquiler = 'RESERVADO' | 'ENTREGADO' | 'DEVUELTO' | 'CANCELADO';

export interface Alquiler {
  id: number;
  herramientaId: number;
  herramientaNombre: string;
  clienteId: number;
  clienteNombre: string;
  fechaInicio: string;
  fechaFin: string;
  dias: number;
  valorTotal: number;
  deposito: number;
  estado: EstadoAlquiler;
  creadoEn: string;
}

// Lo único que se envía al crear un alquiler
export interface NuevoAlquiler {
  herramientaId: number;
  fechaInicio: string;
  fechaFin: string;
}
