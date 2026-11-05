// Los mismos nombres que en el backend (Rol.java y Permiso.java)
export type Rol = 'ADMIN' | 'CLIENTE';

export type Permiso =
  | 'HERRAMIENTAS_VER'
  | 'HERRAMIENTAS_GESTIONAR'
  | 'ALQUILERES_CREAR'
  | 'ALQUILERES_VER_PROPIOS'
  | 'ALQUILERES_CANCELAR_PROPIOS'
  | 'ALQUILERES_VER_TODOS'
  | 'ALQUILERES_GESTIONAR'
  | 'CLIENTES_GESTIONAR'
  | 'REPORTES_VER';
