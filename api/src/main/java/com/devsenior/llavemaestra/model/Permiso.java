package com.devsenior.llavemaestra.model;

import org.springframework.security.core.Authentication;

// Cada permiso es UNA accion concreta del negocio.
// Los roles (Rol.java) solo agrupan permisos.
public enum Permiso {
    HERRAMIENTAS_VER,
    HERRAMIENTAS_GESTIONAR,

    ALQUILERES_CREAR,
    ALQUILERES_VER_PROPIOS,
    ALQUILERES_CANCELAR_PROPIOS,
    ALQUILERES_VER_TODOS,
    ALQUILERES_GESTIONAR,

    CLIENTES_GESTIONAR,
    REPORTES_VER;

    // ¿La persona autenticada tiene este permiso?
    public boolean estaEn(Authentication autenticacion) {
        return autenticacion.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals(name()));
    }
}
