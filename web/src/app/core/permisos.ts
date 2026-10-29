export type Rol = 'ADMIN' | 'USER'

export type Permiso =
| 'HERRAMIENTAS_VER'
| 'HERRAMIENTAS_GESTIONAR'
| 'ALQUILERES_CREAR'
| 'ALQUILERES_VER_PROPIOS'
| 'ALQUILERES_CANCELAR_PROPIOS'
| 'ALQUILERES_CANCELAR_TODAS'
| 'ALQUILERES_VER_TODOS'
| 'CLIENTES_GESTIONAR'
| 'REPORTES_VER';

export const NOMBRE_ROL: Record<Rol, string> ={
    ADMIN: 'Administración',
    USER: "Cliente"
};

export const DESCRIPCION_PERMISO:Record<Permiso,string>={
 HERRAMIENTAS_VER: 'Ver herramientas',
 HERRAMIENTAS_GESTIONAR: 'Gestionar herramientas',
 ALQUILERES_CREAR: 'Alquila',
 ALQUILERES_VER_PROPIOS: 'Ver mis Alquileres',
 ALQUILERES_CANCELAR_PROPIOS: 'Cancelar mis alquileres',
 ALQUILERES_CANCELAR_TODAS: 'Cancelar cualquier reserva',
 ALQUILERES_VER_TODOS: 'Ver todas las reservas',
 CLIENTES_GESTIONAR: 'Gestionar clientes',
 REPORTES_VER: 'Ver reportes'
};

