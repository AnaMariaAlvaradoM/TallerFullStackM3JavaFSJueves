package com.devsenior.llavemaestra.dto;

import java.util.List;

// Lo que la administracion ve en su panel: el mes en curso
public record ResumenResponse(
            long reservasHoy,
        long reservasMes,
        int ingresosMes,
        long miembrosActivos,
        long herraminetasActivas,
        List<OcupacionHerramienta> ocupacionMes
) {

}
