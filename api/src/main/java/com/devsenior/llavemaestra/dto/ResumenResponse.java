package com.devsenior.llavemaestra.dto;

import java.math.BigDecimal;
import java.util.List;

public record ResumenResponse(
        BigDecimal ingresosMes,
        long alquileresActivos,
        List<AlquilerResponse> vencidos,
        List<HerramientaRankingResponse> masAlquiladas
) {
}