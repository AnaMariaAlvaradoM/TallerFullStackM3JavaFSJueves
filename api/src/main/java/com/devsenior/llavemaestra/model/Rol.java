package com.devsenior.llavemaestra.model;

import java.util.Set;
import  static com.devsenior.llavemaestra.model.Permiso.*;

public enum Rol {
ADMIN(Set.of(
    HERRAMIENTAS_VER,
    HERRAMIENTAS_GESTIONAR,
    ALQUILERES_VER_TODOS,
    ALQUILERES_GESTIONAR,
    CLIENTES_GESTIONAR,
    REPORTES_VER
)),

CLIENTE(Set.of(

    HERRAMIENTAS_VER,
    ALQUILERES_CREAR,
    ALQUILERES_VER_PROPIOS,
    ALQUILERES_CANCELAR_PROPIOS


));

private final Set<Permiso> permisos;

Rol(Set<Permiso> permisos) {

    this.permisos = permisos;
}

public Set<Permiso> getPermisos() {
    return permisos;
}

}
