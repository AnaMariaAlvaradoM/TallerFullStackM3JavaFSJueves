package com.devsenior.llavemaestra.exception;

// 409: los datos son validos, pero el negocio no lo permite (ej: no quedan unidades)
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
