package com.devsenior.llavemaestra.model;

import org.springframework.security.core.Authentication;

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

    public boolean estaEn(Authentication authentication){
        return authentication.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals(name()));
    }
}
