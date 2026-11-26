package com.devsenior.llavemaestra.dto;

import java.util.List;

public record ResumenResponse(
    long ingresosMes,
    long alquileresActivos,
    List<AlquilerResponse> vencidos,
    List<HerramientaMasAlquiladaResponse> masAlquiladas
) {}
