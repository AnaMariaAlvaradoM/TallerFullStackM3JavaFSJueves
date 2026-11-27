package com.devsenior.llavemaestra.model;

import java.util.List;

public enum EstadoAlquiler {
    RESERVADO,
    ENTREGADO,
    DEVUELTO,
    CANCELADO;
    
    // Un alquiler "activo" ocupa la herramienta: RESERVADO o ENTREGADO
    public static final List<EstadoAlquiler> ACTIVOS = List.of(RESERVADO, ENTREGADO);
}
