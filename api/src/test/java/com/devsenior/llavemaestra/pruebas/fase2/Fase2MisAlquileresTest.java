package com.devsenior.llavemaestra.pruebas.fase2;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 2.4 · Mis alquileres y cancelar (solo lo propio)
class Fase2MisAlquileresTest extends PruebaApi {

    @Test
    void misAlquileresTraeSoloLosDelCliente() {
        String pedro = token(PEDRO);
        String diana = token(DIANA);
        List<Long> dePedro = new ArrayList<>();
        List<Long> deDiana = new ArrayList<>();
        try {
            dePedro.add(alquilarOk(pedro, idHerramienta("Andamio tubular"), dia(500), dia(501)));
            deDiana.add(alquilarOk(diana, idHerramienta("Andamio tubular"), dia(502), dia(503)));
            Respuesta r = get("/api/alquileres/mios", pedro);
            assertEquals(200, r.estado(), "GET /api/alquileres/mios como cliente debería responder 200");
            List<Long> ids = new ArrayList<>();
            for (JsonNode a : r.json()) {
                ids.add(a.path("id").asLong());
                assertEquals("Pedro Salazar", a.path("clienteNombre").asString(), "Pedro solo debería ver SUS alquileres");
            }
            assertTrue(ids.contains(dePedro.get(0)), "El alquiler que Pedro acaba de crear debería aparecer en /mios");
            assertFalse(ids.contains(deDiana.get(0)), "El alquiler de Diana NO debería aparecer en los de Pedro");
        } finally {
            limpiar(pedro, dePedro);
            limpiar(diana, deDiana);
        }
    }

    @Test
    void elAdministradorNoTieneMisAlquileres() {
        assertEquals(403, get("/api/alquileres/mios", token(ADMIN)).estado(),
                "ADMIN no tiene ALQUILERES_VER_PROPIOS: GET /api/alquileres/mios debería responder 403");
    }

    @Test
    void elClienteCancelaLoSuyoYNoDosVeces() {
        String pedro = token(PEDRO);
        long id = alquilarOk(pedro, idHerramienta("Guadañadora"), dia(510), dia(511));
        Respuesta r = cancelar(pedro, id);
        assertEquals(200, r.estado(), "Pedro debería poder cancelar su propio alquiler (200)");
        assertEquals("CANCELADO", r.json().path("estado").asString(), "Después de cancelar, el estado es CANCELADO");
        Respuesta otraVez = cancelar(pedro, id);
        assertEquals(409, otraVez.estado(), "Cancelar un alquiler ya cancelado debería responder 409");
        tieneMensaje(otraVez, "Ya cancelado");
    }

    @Test
    void pedroNoPuedeCancelarElAlquilerDeDiana() {
        String diana = token(DIANA);
        List<Long> deDiana = new ArrayList<>();
        try {
            deDiana.add(alquilarOk(diana, idHerramienta("Guadañadora"), dia(520), dia(521)));
            Respuesta r = cancelar(token(PEDRO), deDiana.get(0));
            assertEquals(403, r.estado(), "Pedro no debería poder cancelar un alquiler de Diana (403): la regla sobre el dato va en el servicio");
        } finally {
            limpiar(diana, deDiana);
        }
    }

    @Test
    void cancelarUnAlquilerQueNoExisteResponde404() {
        String pedro = token(PEDRO);
        long id = alquilarOk(pedro, idHerramienta("Guadañadora"), dia(530), dia(530));
        assertEquals(200, cancelar(pedro, id).estado(), "Pedro debería poder cancelar su propio alquiler (200)");
        Respuesta r = cancelar(pedro, 999999);
        assertEquals(404, r.estado(), "PATCH /api/alquileres/999999/cancelar debería responder 404");
        tieneMensaje(r, "Alquiler inexistente");
    }

    @Test
    void unAlquilerQueEmpiezaHoySePuedeCrearPeroNoCancelar() {
        String diana = token(DIANA);
        Respuesta r = alquilar(diana, idHerramienta("Andamio tubular"), dia(0), dia(0));
        assertEquals(201, r.estado(), "Alquilar desde hoy es válido (201): hoy no es una fecha pasada. Respondió " + r.estado() + ": " + r.mensaje());
        Respuesta c = cancelar(diana, r.json().path("id").asLong());
        assertEquals(409, c.estado(), "Un alquiler que empieza hoy ya no se puede cancelar (409)");
        tieneMensaje(c, "Empieza hoy");
    }
}
