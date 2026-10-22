package com.devsenior.llavemaestra.pruebas.fase2;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 2.3 · Disponibilidad: existe, esta activa, quedan unidades y el cliente no pasa de 3
class Fase2DisponibilidadTest extends PruebaApi {

    @Test
    void unaHerramientaQueNoExisteResponde404() {
        Respuesta r = alquilar(token(PEDRO), 999999, dia(400), dia(401));
        assertEquals(404, r.estado(), "Alquilar la herramienta 999999 debería responder 404");
        tieneMensaje(r, "Herramienta inexistente");
    }

    @Test
    void unaHerramientaRetiradaNoSeAlquila() {
        Respuesta r = alquilar(token(PEDRO), idHerramienta("Nivel láser"), dia(400), dia(401));
        assertEquals(409, r.estado(), "El Nivel láser está retirado: debería responder 409");
        tieneMensaje(r, "Herramienta retirada");
    }

    @Test
    void laUnicaMezcladoraNoSeAlquilaDosVecesEnFechasCruzadas() {
        String pedro = token(PEDRO);
        String diana = token(DIANA);
        long mezcladora = idHerramienta("Mezcladora de concreto");
        List<Long> dePedro = new ArrayList<>();
        List<Long> deDiana = new ArrayList<>();
        try {
            dePedro.add(alquilarOk(pedro, mezcladora, dia(410), dia(412)));
            Respuesta cruce = alquilar(diana, mezcladora, dia(412), dia(414));
            assertEquals(409, cruce.estado(), "La mezcladora tiene 1 unidad y está alquilada del 410 al 412: "
                    + "Diana no debería poder alquilarla del 412 al 414 (se cruzan el día 412)");
            tieneMensaje(cruce, "Sin unidades");
            deDiana.add(alquilarOk(diana, mezcladora, dia(413), dia(415)));
        } finally {
            limpiar(pedro, dePedro);
            limpiar(diana, deDiana);
        }
    }

    @Test
    void conDosUnidadesSePermitenDosCruzadosPeroNoTres() {
        String pedro = token(PEDRO);
        String diana = token(DIANA);
        long pulidora = idHerramienta("Pulidora angular");
        List<Long> dePedro = new ArrayList<>();
        List<Long> deDiana = new ArrayList<>();
        try {
            dePedro.add(alquilarOk(pedro, pulidora, dia(420), dia(424)));
            deDiana.add(alquilarOk(diana, pulidora, dia(422), dia(426)));
            Respuesta tercero = alquilar(pedro, pulidora, dia(423), dia(423));
            assertEquals(409, tercero.estado(), "La pulidora tiene 2 unidades y ya hay 2 alquileres cruzados el día 423: debería responder 409");
        } finally {
            limpiar(pedro, dePedro);
            limpiar(diana, deDiana);
        }
    }

    @Test
    void unAlquilerCanceladoLiberaLaUnidad() {
        String pedro = token(PEDRO);
        String diana = token(DIANA);
        long mezcladora = idHerramienta("Mezcladora de concreto");
        List<Long> deDiana = new ArrayList<>();
        long dePedro = alquilarOk(pedro, mezcladora, dia(430), dia(431));
        try {
            assertEquals(200, cancelar(pedro, dePedro).estado(), "Pedro debería poder cancelar su alquiler (200)");
            Respuesta r = alquilar(diana, mezcladora, dia(430), dia(431));
            assertEquals(201, r.estado(), "Si Pedro canceló, la mezcladora queda libre: Diana debería poder alquilarla (201). "
                    + "Solo cuentan los alquileres RESERVADO o ENTREGADO");
            deDiana.add(r.json().path("id").asLong());
        } finally {
            limpiar(diana, deDiana);
        }
    }

    @Test
    void unClienteNoPuedeTenerMasDeTresAlquileresActivos() {
        String pedro = token(PEDRO);
        long taladro = idHerramienta("Taladro percutor");
        long activos = 0;
        for (JsonNode a : get("/api/alquileres/mios", pedro).json()) {
            String estado = a.path("estado").asString();
            if (estado.equals("RESERVADO") || estado.equals("ENTREGADO")) activos++;
        }
        List<Long> creados = new ArrayList<>();
        try {
            for (long i = activos; i < 3; i++) {
                creados.add(alquilarOk(pedro, taladro, dia(440 + (int) i * 3), dia(440 + (int) i * 3)));
            }
            Respuesta cuarto = alquilar(pedro, idHerramienta("Andamio tubular"), dia(460), dia(461));
            assertEquals(409, cuarto.estado(), "Pedro ya tiene 3 alquileres activos: el cuarto debería responder 409");
            tieneMensaje(cuarto, "Más de 3 activos");
        } finally {
            limpiar(pedro, creados);
        }
    }
}
