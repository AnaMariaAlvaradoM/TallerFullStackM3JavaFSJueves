package com.devsenior.llavemaestra.model;

import java.util.Set;

// Un rol es un "paquete" de permisos (la matriz de permisos de la Fase 1)
public enum Rol {

    ADMIN(Set.of(
            Permiso.HERRAMIENTAS_VER,
            Permiso.HERRAMIENTAS_GESTIONAR,
            Permiso.ALQUILERES_VER_TODOS,
            Permiso.ALQUILERES_GESTIONAR,
            Permiso.CLIENTES_GESTIONAR,
            Permiso.REPORTES_VER)),

    CLIENTE(Set.of(
            Permiso.HERRAMIENTAS_VER,
            Permiso.ALQUILERES_CREAR,
            Permiso.ALQUILERES_VER_PROPIOS,
            Permiso.ALQUILERES_CANCELAR_PROPIOS));

    private final Set<Permiso> permisos;

    Rol(Set<Permiso> permisos) {
        this.permisos = permisos;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }
}
