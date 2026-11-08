package com.devsenior.llavemaestra.pruebas.fase3;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.*;

// 3.4 · Crear y editar herramientas (HERRAMIENTAS_GESTIONAR)
class Fase3HerramientasTest extends PruebaApi {

    private static String json(String nombre, String categoria, int precioDia, int deposito, int unidades) {
        return "{\"nombre\":\"" + nombre + "\",\"categoria\":\"" + categoria + "\",\"descripcion\":\"Para pruebas\","
                + "\"precioDia\":" + precioDia + ",\"deposito\":" + deposito + ",\"unidades\":" + unidades
                + ",\"imagenUrl\":\"https://loremflickr.com/800/500/tool?lock=1\"}";
    }

    private static String nombreNuevo(String base) {
        return base + " " + System.nanoTime();
    }

    private long crearOk(String nombre) {
        Respuesta r = post("/api/herramientas", token(ADMIN), json(nombre, "CONSTRUCCION", 18000, 90000, 2));
        assertEquals(201, r.estado(), "POST /api/herramientas válido como administrador debería responder 201. Respondió "
                + r.estado() + ": " + r.mensaje());
        return r.json().path("id").asLong();
    }

    @Test
    void losClientesVenLaHerramientaNueva() {
        String nombre = nombreNuevo("Cortadora de baldosa");
        Respuesta r = post("/api/herramientas", token(ADMIN), json(nombre, "CONSTRUCCION", 18000, 90000, 2));
        assertEquals(201, r.estado(), "POST /api/herramientas válido debería responder 201. Respondió " + r.estado() + ": " + r.mensaje());
        JsonNode h = r.json();
        assertEquals(nombre, h.path("nombre").asString(), "La respuesta debería traer el nombre enviado");
        assertEquals(18000, h.path("precioDia").asInt(), "La respuesta debería traer el precioDia enviado");
        assertEquals(2, h.path("unidades").asInt(), "La respuesta debería traer las unidades enviadas");
        assertTrue(h.path("activa").asBoolean(), "Una herramienta nueva entra activa");
        assertTrue(nombres(get("/api/herramientas", token(PEDRO)).json()).contains(nombre), "Pedro debería ver la herramienta nueva en el catálogo");
    }

    @Test
    void hernanEditaUnaHerramienta() {
        long id = crearOk(nombreNuevo("Compresor"));
        String nombre = nombreNuevo("Compresor 50 L");
        Respuesta r = put("/api/herramientas/" + id, token(ADMIN), json(nombre, "ELECTRICA", 20000, 120000, 3));
        assertEquals(200, r.estado(), "PUT /api/herramientas/{id} válido debería responder 200. Respondió " + r.estado() + ": " + r.mensaje());
        JsonNode guardada = get("/api/herramientas/" + id, token(ADMIN)).json();
        assertEquals(nombre, guardada.path("nombre").asString(), "Después del PUT, GET /api/herramientas/{id} debería traer el nombre nuevo");
        assertEquals(20000, guardada.path("precioDia").asInt(), "Después del PUT, el precioDia debería ser 20000");
        assertEquals(3, guardada.path("unidades").asInt(), "Después del PUT, las unidades deberían ser 3");
        assertEquals("ELECTRICA", guardada.path("categoria").asString(), "Después del PUT, la categoría debería ser ELECTRICA");
    }

    @Test
    void pedroNoCreaNiEditaHerramientas() {
        assertEquals(403, post("/api/herramientas", token(PEDRO), json(nombreNuevo("Pirata"), "JARDIN", 5000, 0, 1)).estado(),
                "Pedro no tiene HERRAMIENTAS_GESTIONAR: POST /api/herramientas debería responder 403");
        assertEquals(403, put("/api/herramientas/" + idHerramienta("Taladro percutor"), token(PEDRO),
                json("Taladro percutor", "ELECTRICA", 1000, 0, 3)).estado(),
                "Pedro no tiene HERRAMIENTAS_GESTIONAR: PUT /api/herramientas/{id} debería responder 403");
    }

    @Test
    void losDatosInvalidosResponden400() {
        String admin = token(ADMIN);
        Respuesta precio = post("/api/herramientas", admin, json(nombreNuevo("Barata"), "JARDIN", 0, 0, 1));
        assertEquals(400, precio.estado(), "Un precioDia menor a 1.000 debería responder 400");
        tieneMensaje(precio, "Precio inválido");
        assertEquals(400, post("/api/herramientas", admin, json(nombreNuevo("Sin unidades"), "JARDIN", 5000, 0, 0)).estado(),
                "Una herramienta con 0 unidades debería responder 400");
        assertEquals(400, post("/api/herramientas", admin, json("", "JARDIN", 5000, 0, 1)).estado(),
                "Una herramienta sin nombre debería responder 400");
    }

    @Test
    void noSeRepiteElNombreNiSeEditaLoInexistente() {
        String admin = token(ADMIN);
        Respuesta repetida = post("/api/herramientas", admin, json("Taladro percutor", "ELECTRICA", 25000, 100000, 1));
        assertEquals(409, repetida.estado(), "Ya existe el \"Taladro percutor\": crear otra con el mismo nombre debería responder 409");
        tieneMensaje(repetida, "Nombre repetido");
        assertEquals(404, put("/api/herramientas/999999", admin, json(nombreNuevo("Fantasma"), "JARDIN", 5000, 0, 1)).estado(),
                "PUT /api/herramientas/999999 debería responder 404");
    }
}
