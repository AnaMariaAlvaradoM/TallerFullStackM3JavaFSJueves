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

export const NOMBRE_ROL : Record<Rol, string> = {
    ADMIN: 'Administración',
    CLIENTE: 'Cliente'
};

export const DESCRIPCION_PERMISO : Record<Permiso, string> ={
    HERRAMIENTAS_VER: 'Ver herramientas',
    HERRAMIENTAS_GESTIONAR: 'Gestionar herramientas',
    ALQUILERES_CREAR: 'Alquilar',
    ALQUILERES_VER_PROPIOS:'Ver mis alquileres',
    ALQUILERES_CANCELAR_PROPIOS: 'Cancelar mis alquileres',
    ALQUILERES_VER_TODOS: 'Ver todos los alquileres',
    ALQUILERES_GESTIONAR: 'Gestionar alquileres',
    CLIENTES_GESTIONAR: 'Gestionar clientes',
    REPORTES_VER: 'Ver reportes'
};
