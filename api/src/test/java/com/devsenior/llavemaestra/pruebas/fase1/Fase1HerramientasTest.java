package com.devsenior.llavemaestra.pruebas.fase1;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 1.2 · Catalogo de herramientas
class Fase1HerramientasTest extends PruebaApi {

    private static final List<String> ACTIVAS = List.of("Taladro percutor", "Pulidora angular",
            "Mezcladora de concreto", "Andamio tubular", "Guadañadora");

    @Test
    void sinTokenElCatalogoResponde401() {
        assertEquals(200, get("/api/herramientas", token(PEDRO)).estado(), "GET /api/herramientas con token debería responder 200");
        assertEquals(401, get("/api/herramientas", null).estado(), "GET /api/herramientas sin token debería responder 401");
    }

    @Test
    void elAdministradorVeLasSeisHerramientasIncluidaLaRetirada() {
        Respuesta r = get("/api/herramientas", token(ADMIN));
        assertEquals(200, r.estado(), "GET /api/herramientas como administrador debería responder 200");
        List<String> nombres = nombres(r.json());
        for (String nombre : ACTIVAS) {
            assertTrue(nombres.contains(nombre), "El administrador debería ver \"" + nombre + "\"");
        }
        assertTrue(nombres.contains("Nivel láser"), "El administrador también debería ver el Nivel láser (retirado)");
    }

    @Test
    void elClienteSoloVeLasHerramientasActivas() {
        Respuesta r = get("/api/herramientas", token(PEDRO));
        assertEquals(200, r.estado(), "GET /api/herramientas como cliente debería responder 200 (permiso HERRAMIENTAS_VER)");
        List<String> nombres = nombres(r.json());
        assertTrue(nombres.containsAll(ACTIVAS), "Pedro debería ver las 5 herramientas activas");
        assertFalse(nombres.contains("Nivel láser"), "Pedro NO debería ver el Nivel láser: está retirado");
        r.json().forEach(h -> assertTrue(h.path("activa").asBoolean(), "El cliente solo debería recibir herramientas con activa = true"));
    }

    @Test
    void cadaHerramientaTraeLosCamposDelContrato() {
        JsonNode taladro = herramienta("Taladro percutor");
        assertTrue(taladro.path("id").isNumber(), "Cada herramienta debería traer su id");
        assertEquals("ELECTRICA", taladro.path("categoria").asString(), "La categoría del taladro debería ser ELECTRICA");
        assertEquals(25000, taladro.path("precioDia").asInt(), "El precioDia del taladro debería ser 25000");
        assertEquals(100000, taladro.path("deposito").asInt(), "El deposito del taladro debería ser 100000");
        assertEquals(3, taladro.path("unidades").asInt(), "El taladro debería tener 3 unidades");
        assertTrue(taladro.path("activa").asBoolean(), "El taladro debería estar activa = true");
    }

    @Test
    void seConsultaUnaHerramientaPorIdY404SiNoExiste() {
        long id = idHerramienta("Mezcladora de concreto");
        Respuesta r = get("/api/herramientas/" + id, token(PEDRO));
        assertEquals(200, r.estado(), "GET /api/herramientas/{id} debería responder 200");
        assertEquals("Mezcladora de concreto", r.json().path("nombre").asString(), "GET /api/herramientas/{id} debería traer esa herramienta");
        Respuesta noExiste = get("/api/herramientas/999999", token(PEDRO));
        assertEquals(404, noExiste.estado(), "GET /api/herramientas/999999 debería responder 404");
        tieneMensaje(noExiste, "Herramienta inexistente");
    }
}
