package com.devsenior.llavemaestra.model;

import java.util.EnumSet;
import java.util.Set;

public enum Rol {
    ADMIN(EnumSet.allOf(Permiso.class)),
    CLIENTE(EnumSet.of(
            Permiso.HERRAMIENTAS_VER,
            Permiso.ALQUILERES_CREAR,
            Permiso.ALQUILERES_VER_PROPIOS,
            Permiso.ALQUILERES_CANCELAR_PROPIOS
    ));

    private final Set<Permiso> permisos;

    Rol(Set<Permiso> permisos) {
        this.permisos = permisos;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }
}