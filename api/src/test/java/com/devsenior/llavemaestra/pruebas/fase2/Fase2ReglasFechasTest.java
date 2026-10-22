package com.devsenior.llavemaestra.pruebas.fase2;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 2.2 · Reglas de fechas (400 Bad Request)
class Fase2ReglasFechasTest extends PruebaApi {

    @Test
    void laFechaDeFinNoPuedeSerAnteriorALaDeInicio() {
        Respuesta r = alquilar(token(PEDRO), idHerramienta("Taladro percutor"), dia(330), dia(328));
        assertEquals(400, r.estado(), "Fin (día 328) antes de inicio (día 330) debería responder 400");
        tieneMensaje(r, "Fechas invertidas");
    }

    @Test
    void noSeAlquilaDesdeUnaFechaQueYaPaso() {
        Respuesta r = alquilar(token(PEDRO), idHerramienta("Taladro percutor"), dia(-1), dia(1));
        assertEquals(400, r.estado(), "Empezar ayer debería responder 400");
        tieneMensaje(r, "Fecha pasada");
    }

    @Test
    void dieciseisDiasEsDemasiado() {
        Respuesta r = alquilar(token(PEDRO), idHerramienta("Andamio tubular"), dia(340), dia(355));
        assertEquals(400, r.estado(), "Del día 340 al 355 son 16 días: debería responder 400 (máximo 15)");
        tieneMensaje(r, "Más de 15 días");
    }

    @Test
    void quinceDiasExactosSiSePuede() {
        String pedro = token(PEDRO);
        List<Long> creados = new ArrayList<>();
        try {
            Respuesta r = alquilar(pedro, idHerramienta("Andamio tubular"), dia(360), dia(374));
            assertEquals(201, r.estado(), "Del día 360 al 374 son 15 días exactos: es el límite y SÍ se puede (201). "
                    + "Respondió " + r.estado() + " (revisa si comparaste con > o con >=, y cómo cuentas los días)");
            creados.add(r.json().path("id").asLong());
            assertEquals(15, r.json().path("dias").asInt(), "Del día 360 al 374 son 15 días");
        } finally {
            limpiar(pedro, creados);
        }
    }

    @Test
    void faltanDatosResponde400ConMensaje() {
        Respuesta r = post("/api/alquileres", token(PEDRO),
                "{\"herramientaId\":" + idHerramienta("Taladro percutor") + ",\"fechaFin\":\"" + dia(380) + "\"}");
        assertEquals(400, r.estado(), "Sin fechaInicio debería responder 400 (usa @NotNull y @Valid)");
        tieneMensaje(r, "Falta la fecha de inicio");
    }
}
