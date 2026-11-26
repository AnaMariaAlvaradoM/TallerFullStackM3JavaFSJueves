package com.devsenior.llavemaestra.pruebas.fase3;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 3.2 · El panel de Hernan: ingresos del mes, activos, vencidos y mas alquiladas
class Fase3ResumenTest extends PruebaApi {

    private JsonNode resumenOk() {
        Respuesta r = resumen(token(ADMIN));
        assertEquals(200, r.estado(), "GET /api/reportes/resumen como administrador debería responder 200 (permiso REPORTES_VER)");
        return r.json();
    }

    private long vecesAlquilada(JsonNode resumen, String herramienta) {
        for (JsonNode fila : resumen.path("masAlquiladas")) {
            if (herramienta.equals(fila.path("herramienta").asString())) {
                return fila.path("alquileres").asLong();
            }
        }
        return 0;
    }

    private boolean estaVencido(JsonNode resumen, long alquilerId) {
        for (JsonNode a : resumen.path("vencidos")) {
            if (a.path("id").asLong() == alquilerId) return true;
        }
        return false;
    }

    @Test
    void soloQuienTieneReportesVerVeElResumen() {
        assertEquals(403, resumen(token(PEDRO)).estado(), "Pedro no tiene REPORTES_VER: GET /api/reportes/resumen debería responder 403");
        assertEquals(401, resumen(null).estado(), "GET /api/reportes/resumen sin token debería responder 401");
    }

    @Test
    void laPulidoraDeJorgeApareceVencida() {
        JsonNode r = resumenOk();
        assertTrue(r.path("vencidos").isArray(), "El resumen debería traer \"vencidos\" como una lista de alquileres");
        boolean encontrada = false;
        for (JsonNode a : r.path("vencidos")) {
            if ("Pulidora angular".equals(a.path("herramientaNombre").asString())
                    && "Jorge Pinzón".equals(a.path("clienteNombre").asString())) {
                encontrada = true;
                assertEquals("ENTREGADO", a.path("estado").asString(), "Un vencido está ENTREGADO (no lo han devuelto)");
            }
        }
        assertTrue(encontrada, "En \"vencidos\" debería estar la Pulidora angular de Jorge Pinzón: entregada y con fecha de fin pasada (siémbrala en DataInitializer)");
    }

    @Test
    void unEntregadoQueTerminaHoyTodaviaNoEstaVencido() {
        String pedro = token(PEDRO);
        long id = alquilarOk(pedro, idHerramienta("Andamio tubular"), dia(0), dia(0));
        try {
            assertEquals(200, entregar(token(ADMIN), id).estado(), "Hernán debería poder entregar el alquiler de hoy (200)");
            assertFalse(estaVencido(resumenOk(), id), "Un alquiler que termina HOY todavía no está vencido: vence cuando la fecha de fin ya pasó");
        } finally {
            cerrar(id);
        }
    }

    @Test
    void losIngresosDelMesNoCuentanLoCancelado() {
        String pedro = token(PEDRO);
        int antes = resumenOk().path("ingresosMes").asInt();
        long id = alquilarOk(pedro, idHerramienta("Taladro percutor"), dia(620), dia(621));
        try {
            assertEquals(antes + 50000, resumenOk().path("ingresosMes").asInt(),
                    "Un alquiler creado este mes de 2 días de taladro (50.000) debería sumar 50.000 a ingresosMes");
        } finally {
            cancelar(pedro, id);
        }
        assertEquals(antes, resumenOk().path("ingresosMes").asInt(), "Al cancelarlo, ingresosMes debería volver a su valor: los cancelados no cuentan");
    }

    @Test
    void losActivosSubenAlCrearBajanAlCancelar() {
        String pedro = token(PEDRO);
        long antes = resumenOk().path("alquileresActivos").asLong();
        long id = alquilarOk(pedro, idHerramienta("Taladro percutor"), dia(625), dia(625));
        try {
            assertEquals(antes + 1, resumenOk().path("alquileresActivos").asLong(), "Un alquiler RESERVADO nuevo debería sumar 1 a alquileresActivos");
        } finally {
            cancelar(pedro, id);
        }
        assertEquals(antes, resumenOk().path("alquileresActivos").asLong(), "Un alquiler CANCELADO ya no es activo");
    }

    @Test
    void lasMasAlquiladasNoCuentanCancelados() {
        String pedro = token(PEDRO);
        long antes = vecesAlquilada(resumenOk(), "Guadañadora");
        List<Long> creados = new ArrayList<>();
        try {
            creados.add(alquilarOk(pedro, idHerramienta("Guadañadora"), dia(630), dia(630)));
            creados.add(alquilarOk(pedro, idHerramienta("Guadañadora"), dia(640), dia(640)));
            assertEquals(200, cancelar(pedro, creados.get(1)).estado(), "Pedro debería poder cancelar su alquiler (200)");
            JsonNode r = resumenOk();
            assertEquals(antes + 1, vecesAlquilada(r, "Guadañadora"),
                    "En masAlquiladas la Guadañadora debería sumar 1 (dos alquileres, uno cancelado: los cancelados no cuentan)");
            long anterior = Long.MAX_VALUE;
            for (JsonNode fila : r.path("masAlquiladas")) {
                long veces = fila.path("alquileres").asLong();
                assertTrue(veces <= anterior, "masAlquiladas debería ir de la más alquilada a la menos alquilada");
                anterior = veces;
            }
        } finally {
            limpiar(pedro, creados);
        }
    }
}
