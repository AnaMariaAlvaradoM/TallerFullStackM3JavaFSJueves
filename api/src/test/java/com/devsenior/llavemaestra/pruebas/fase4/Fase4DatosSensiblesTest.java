package com.devsenior.llavemaestra.pruebas.fase4;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

// 4.2 · Seguridad final: ninguna respuesta entrega datos que no deberia
class Fase4DatosSensiblesTest extends PruebaApi {

    private static final String[] CAMPOS_PROHIBIDOS = {"password", "contrasena", "contraseña", "clave", "hash"};

    private void sinClaves(Respuesta r, String donde) {
        assertEquals(200, r.estado(), donde + " debería responder 200");
        String cuerpo = r.cuerpo();
        assertFalse(cuerpo.contains("$2a$") || cuerpo.contains("$2b$") || cuerpo.contains("$2y$"),
                donde + " está devolviendo una contraseña cifrada con BCrypt ($2a$…). Nunca se devuelve, ni siquiera cifrada");
        String minusculas = cuerpo.toLowerCase(Locale.ROOT);
        for (String campo : CAMPOS_PROHIBIDOS) {
            assertFalse(minusculas.contains("\"" + campo + "\""), donde + " trae el campo \"" + campo
                    + "\". Usa un DTO de salida que no lo tenga");
        }
    }

    private void sinDetallesInternos(Respuesta r, String donde) {
        String cuerpo = r.cuerpo() == null ? "" : r.cuerpo();
        for (String pista : new String[]{"\"trace\"", "\"exception\"", "Exception", "at com.", "at org.", "java.lang"}) {
            assertFalse(cuerpo.contains(pista), donde + " deja ver detalles internos del servidor (" + pista
                    + "). El error debería ser solo { codigo, mensaje, fecha }");
        }
        JsonNode json = r.json();
        assertTrue(json != null && json.has("codigo") && json.has("mensaje") && json.has("fecha"),
                donde + " debería responder con el formato { codigo, mensaje, fecha }. Respondió: " + resumir(cuerpo));
    }

    @Test
    void laListaDeClientesNoTraeContrasenas() {
        sinClaves(get("/api/usuarios", token(ADMIN)), "GET /api/usuarios");
    }

    @Test
    void losAlquileresNoTraenDatosDelUsuario() {
        sinClaves(get("/api/alquileres", token(ADMIN)), "GET /api/alquileres");
        sinClaves(get("/api/alquileres/mios", token(PEDRO)), "GET /api/alquileres/mios");
    }

    @Test
    void elLoginSoloRespondeElToken() {
        Respuesta r = login(PEDRO, CLAVE);
        sinClaves(r, "POST /api/auth/login");
        JsonNode json = r.json();
        assertTrue(json.has("token"), "El login debería responder { \"token\": \"...\" }");
        JsonNode payload = payload(json.path("token").asString());
        String texto = payload.toString().toLowerCase(Locale.ROOT);
        assertFalse(texto.contains("password") || texto.contains("$2a$"),
                "El token no debería llevar la contraseña: cualquiera puede leer su payload");
    }

    @Test
    void elLoginNoRevelaSiElEmailExiste() {
        token(PEDRO);
        Respuesta claveMala = login(PEDRO, "clave-equivocada-123");
        Respuesta noExiste = login("no-existe-" + System.nanoTime() + "@correo.com", "clave-equivocada-123");
        assertEquals(401, claveMala.estado(), "Login con contraseña equivocada debería responder 401");
        assertEquals(401, noExiste.estado(), "Login con un email que no existe debería responder 401 (no 404)");
        assertEquals(claveMala.mensaje(), noExiste.mensaje(),
                "El mensaje debería ser el MISMO con clave equivocada y con email inexistente; si no, cualquiera averigua qué emails están registrados");
    }

    @Test
    void unJsonMalArmadoNoMuestraElError() {
        Respuesta r = post("/api/alquileres", token(PEDRO), "{\"herramientaId\": 1, \"fechaInicio\": ");
        assertEquals(400, r.estado(), "Un JSON mal armado debería responder 400. Respondió " + r.estado());
        sinDetallesInternos(r, "POST /api/alquileres con un JSON mal armado");
    }

    @Test
    void unIdQueNoEsNumeroNoMuestraElError() {
        Respuesta r = patch("/api/alquileres/abc/entregar", token(ADMIN), null);
        assertEquals(400, r.estado(), "PATCH /api/alquileres/abc/entregar (id que no es número) debería responder 400. Respondió " + r.estado());
        sinDetallesInternos(r, "PATCH /api/alquileres/abc/entregar");
    }
}
