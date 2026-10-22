package com.devsenior.llavemaestra.exception;

// 400: los datos no tienen sentido para el negocio (ej: fecha de fin antes de la de inicio)
public class DatoInvalidoException extends RuntimeException {

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
