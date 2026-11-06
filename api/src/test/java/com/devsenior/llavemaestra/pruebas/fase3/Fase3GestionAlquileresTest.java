package com.devsenior.llavemaestra.pruebas.fase3;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 3.1 · La vista de Hernan: todos los alquileres, entregar y devolver
class Fase3GestionAlquileresTest extends PruebaApi {

    private List<Long> ids(JsonNode lista) {
        List<Long> ids = new ArrayList<>();
        lista.forEach(a -> ids.add(a.path("id").asLong()));
        return ids;
    }

    @Test
    void hernanVeLosAlquileresDeTodosLosClientes() {
        String pedro = token(PEDRO);
        String diana = token(DIANA);
        List<Long> dePedro = new ArrayList<>();
        List<Long> deDiana = new ArrayList<>();
        try {
            dePedro.add(alquilarOk(pedro, idHerramienta("Andamio tubular"), dia(600), dia(601)));
            deDiana.add(alquilarOk(diana, idHerramienta("Andamio tubular"), dia(602), dia(603)));
            Respuesta r = get("/api/alquileres", token(ADMIN));
            assertEquals(200, r.estado(), "GET /api/alquileres como administrador debería responder 200 (permiso ALQUILERES_VER_TODOS)");
            List<Long> ids = ids(r.json());
            assertTrue(ids.contains(dePedro.get(0)), "Hernán debería ver el alquiler de Pedro en GET /api/alquileres");
            assertTrue(ids.contains(deDiana.get(0)), "Hernán debería ver el alquiler de Diana en GET /api/alquileres");
        } finally {
            limpiar(pedro, dePedro);
            limpiar(diana, deDiana);
        }
    }

    @Test
    void elFiltroPorEstadoSoloTraeEseEstado() {
        Respuesta r = get("/api/alquileres?estado=ENTREGADO", token(ADMIN));
        assertEquals(200, r.estado(), "GET /api/alquileres?estado=ENTREGADO debería responder 200");
        assertTrue(r.json().size() > 0, "Debería haber al menos un ENTREGADO: la Pulidora angular que Jorge no ha devuelto (siémbrala en DataInitializer)");
        r.json().forEach(a -> assertEquals("ENTREGADO", a.path("estado").asString(), "Con ?estado=ENTREGADO solo deberían llegar alquileres ENTREGADO"));
    }

    @Test
    void pedroNoVeLosAlquileresDeTodos() {
        assertEquals(403, get("/api/alquileres", token(PEDRO)).estado(),
                "Pedro no tiene ALQUILERES_VER_TODOS: GET /api/alquileres debería responder 403");
    }

    @Test
    void primeroSeEntregaDespuesSeDevuelve() {
        String pedro = token(PEDRO);
        String admin = token(ADMIN);
        long id = alquilarOk(pedro, idHerramienta("Taladro percutor"), dia(610), dia(611));
        boolean cerrado = false;
        try {
            Respuesta antes = devolver(admin, id);
            assertEquals(409, antes.estado(), "No se puede devolver algo que no se entregó: debería responder 409");
            tieneMensaje(antes, "Devolver sin entregar");

            Respuesta entrega = entregar(admin, id);
            assertEquals(200, entrega.estado(), "Hernán debería poder entregar un alquiler RESERVADO (200)");
            assertEquals("ENTREGADO", entrega.json().path("estado").asString(), "Después de entregar, el estado es ENTREGADO");
            assertEquals(409, entregar(admin, id).estado(), "Entregar dos veces el mismo alquiler debería responder 409");

            Respuesta devolucion = devolver(admin, id);
            assertEquals(200, devolucion.estado(), "Hernán debería poder recibir la devolución de un alquiler ENTREGADO (200)");
            assertEquals("DEVUELTO", devolucion.json().path("estado").asString(), "Después de devolver, el estado es DEVUELTO");
            cerrado = true;
        } finally {
            if (!cerrado) {
                cancelar(pedro, id);
                cerrar(id);
            }
        }
    }

    @Test
    void pedroNoPuedeEntregarNiDevolver() {
        String pedro = token(PEDRO);
        List<Long> creados = new ArrayList<>();
        try {
            creados.add(alquilarOk(pedro, idHerramienta("Taladro percutor"), dia(615), dia(615)));
            assertEquals(403, entregar(pedro, creados.get(0)).estado(),
                    "Pedro no tiene ALQUILERES_GESTIONAR: PATCH /api/alquileres/{id}/entregar debería responder 403");
            assertEquals(403, devolver(pedro, creados.get(0)).estado(),
                    "Pedro no tiene ALQUILERES_GESTIONAR: PATCH /api/alquileres/{id}/devolver debería responder 403");
        } finally {
            limpiar(pedro, creados);
        }
    }

    @Test
    void unCanceladoNoSeEntregaNiUnoInexistente() {
        String pedro = token(PEDRO);
        String admin = token(ADMIN);
        long id = alquilarOk(pedro, idHerramienta("Guadañadora"), dia(618), dia(618));
        assertEquals(200, cancelar(pedro, id).estado(), "Pedro debería poder cancelar su alquiler (200)");
        Respuesta r = entregar(admin, id);
        assertEquals(409, r.estado(), "Un alquiler CANCELADO no se puede entregar: debería responder 409");
        tieneMensaje(r, "Entregar un cancelado");
        assertEquals(404, entregar(admin, 999999).estado(), "PATCH /api/alquileres/999999/entregar debería responder 404");
    }
}
