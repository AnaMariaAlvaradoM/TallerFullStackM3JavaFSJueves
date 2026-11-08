package com.devsenior.llavemaestra.model;

// Cada accion del sistema que se puede permitir o negar.
// El token lleva la lista de permisos y los controladores los piden con hasAuthority('...')
public enum Permiso {
    HERRAMIENTAS_VER,
    HERRAMIENTAS_GESTIONAR,
    ALQUILERES_CREAR,
    ALQUILERES_VER_PROPIOS,
    ALQUILERES_CANCELAR_PROPIOS,
    ALQUILERES_VER_TODOS,
    ALQUILERES_GESTIONAR,
    CLIENTES_GESTIONAR,
    REPORTES_VER
}
