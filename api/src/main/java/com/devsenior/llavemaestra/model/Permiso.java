package com.devsenior.llavemaestra.model;

import org.springframework.security.core.Authentication;

public enum Permiso {
    HERRAMIENTAS_VER, // Ambos tienen este permiso
    HERRAMIENTAS_GESTIONAR, //Solo ADMIN

    //-----------Solo CLiente ------------
    ALQUILERES_CREAR,
    ALQUILERES_VER_PROPIOS,
    ALQUILERES_CANCELAR_PROPIOS,
    ALQUILERES_CANCELAR_TODAS,

    //-----------Solo Admin ------------
    ALQUILERES_VER_TODOS,
    ALQUILERES_GESTIONAR,
    CLIENTES_GESTIONAR,
    REPORTES_VER;

    public boolean estaEn(Authentication authentication) {
        return  authentication.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals(name()));
    }
}
