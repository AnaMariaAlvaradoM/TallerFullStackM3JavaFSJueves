package com.devsenior.llavemaestra.model;

import java.util.List;

// El ciclo de vida de un alquiler
public enum EstadoAlquiler {
    RESERVADO,
    ENTREGADO,
    DEVUELTO,
    CANCELADO;

    // Un alquiler "activo" ocupa la herramienta: RESERVADO o ENTREGADO
    public static final List<EstadoAlquiler> ACTIVOS = List.of(RESERVADO, ENTREGADO);
}
